/*
 * Copyright (C) 2019 The Turms Project
 * https://github.com/turms-im/turms
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.turms.server.common.infra.lang;

import im.turms.server.common.infra.exception.IncompatibleJvmException;
import im.turms.server.common.infra.reflect.ReflectionUtil;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import jakarta.annotation.Nullable;

/**
 * @author James Chen
 */
public final class ClassUtil {

    private static final MethodHandle GET_ENUM_CONSTANTS;

    static {
        try {
            GET_ENUM_CONSTANTS = ReflectionUtil
                    .method2Handle(Class.class.getDeclaredMethod("getEnumConstantsShared"));
        } catch (NoSuchMethodException e) {
            throw new IncompatibleJvmException("Could not find the method: java.lang.Class#getEnumConstantsShared", e);
        }
        enum TestEnum {A, B, C}
        TestEnum[] actual = getSharedEnumConstants(TestEnum.class);
        TestEnum[] expected = TestEnum.values();
        if (!Arrays.equals(actual, expected)) {
            throw new IncompatibleJvmException("Expecting the enum constants " +
                    Arrays.toString(actual) +
                    " to be: " +
                    Arrays.toString(expected));
        }
    }

    private ClassUtil() {
    }

    @Nullable
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean exists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static List<Field> getNonStaticFields(Class<?> clazz) {
        List<Field> fields = new LinkedList<>();
        Class<?> currentClass = clazz;
        do {
            for (Field field : currentClass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    fields.add(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null);
        return fields;
    }

    @Nullable
    public static Class<?> getElementClass(Type type) {
        return type instanceof ParameterizedType parameterizedType
                ? (Class<?>) parameterizedType.getActualTypeArguments()[0]
                : null;
    }

    public static Class<?> getIterableElementClass(Field field) {
        return getElementClass(field.getGenericType());
    }

    public static Pair<Class<?>, Class<?>> getMapKeyClassAndElementClass(Field field) {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Type[] actualTypes = parameterizedType.getActualTypeArguments();
        return Pair.of((Class<?>) actualTypes[0], (Class<?>) actualTypes[1]);
    }

    public static <T extends Enum<T>> T[] getSharedEnumConstants(Class<T> clazz) {
        try {
            return (T[]) (Object[]) GET_ENUM_CONSTANTS.invokeExact(clazz);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Failed to get the enum constants of the class: " + clazz, e);
        }
    }

    public static boolean isListOf(Type type, Class<?> elementClass) {
        if (!(type instanceof ParameterizedType parameterizedType &&
                parameterizedType.getRawType() instanceof Class<?> rawType &&
                List.class.isAssignableFrom(rawType))) {
            return false;
        }
        Type[] arguments = parameterizedType.getActualTypeArguments();
        return arguments.length == 1 &&
                arguments[0] instanceof Class<?> argClass &&
                elementClass.isAssignableFrom(argClass);
    }

    public static String getReference(Field field) {
        return field.getDeclaringClass().getTypeName() + "." + field.getName();
    }

}