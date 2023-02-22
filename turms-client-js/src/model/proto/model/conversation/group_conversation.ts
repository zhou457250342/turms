/* eslint-disable */
import Long from "long";
import _m0 from "protobufjs/minimal";

export const protobufPackage = "im.turms.proto";

export interface GroupConversation {
  groupId: string;
  unReadMsgCount: string;
  hasMsgUnRead: boolean;
  memberIdToReadDate: { [key: string]: string };
}

export interface GroupConversation_MemberIdToReadDateEntry {
  key: string;
  value: string;
}

function createBaseGroupConversation(): GroupConversation {
  return { groupId: "0", unReadMsgCount: "0", hasMsgUnRead: false, memberIdToReadDate: {} };
}

export const GroupConversation = {
  encode(message: GroupConversation, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.groupId !== "0") {
      writer.uint32(8).int64(message.groupId);
    }
    if (message.unReadMsgCount !== "0") {
      writer.uint32(16).int64(message.unReadMsgCount);
    }
    if (message.hasMsgUnRead === true) {
      writer.uint32(24).bool(message.hasMsgUnRead);
    }
    Object.entries(message.memberIdToReadDate).forEach(([key, value]) => {
      GroupConversation_MemberIdToReadDateEntry.encode({ key: key as any, value }, writer.uint32(34).fork()).ldelim();
    });
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): GroupConversation {
    const reader = input instanceof _m0.Reader ? input : new _m0.Reader(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseGroupConversation();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.groupId = longToString(reader.int64() as Long);
          break;
        case 2:
          message.unReadMsgCount = longToString(reader.int64() as Long);
          break;
        case 3:
          message.hasMsgUnRead = reader.bool();
          break;
        case 4:
          const entry4 = GroupConversation_MemberIdToReadDateEntry.decode(reader, reader.uint32());
          if (entry4.value !== undefined) {
            message.memberIdToReadDate[entry4.key] = entry4.value;
          }
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
};

function createBaseGroupConversation_MemberIdToReadDateEntry(): GroupConversation_MemberIdToReadDateEntry {
  return { key: "0", value: "0" };
}

export const GroupConversation_MemberIdToReadDateEntry = {
  encode(message: GroupConversation_MemberIdToReadDateEntry, writer: _m0.Writer = _m0.Writer.create()): _m0.Writer {
    if (message.key !== "0") {
      writer.uint32(8).int64(message.key);
    }
    if (message.value !== "0") {
      writer.uint32(16).int64(message.value);
    }
    return writer;
  },

  decode(input: _m0.Reader | Uint8Array, length?: number): GroupConversation_MemberIdToReadDateEntry {
    const reader = input instanceof _m0.Reader ? input : new _m0.Reader(input);
    let end = length === undefined ? reader.len : reader.pos + length;
    const message = createBaseGroupConversation_MemberIdToReadDateEntry();
    while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
        case 1:
          message.key = longToString(reader.int64() as Long);
          break;
        case 2:
          message.value = longToString(reader.int64() as Long);
          break;
        default:
          reader.skipType(tag & 7);
          break;
      }
    }
    return message;
  },
};

function longToString(long: Long) {
  return long.toString();
}

if (_m0.util.Long !== Long) {
  _m0.util.Long = Long as any;
  _m0.configure();
}
