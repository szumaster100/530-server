// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Management.proto

package proto.management;

/**
 * Protobuf type {@code management.ClanMessage}
 */
public final class ClanMessage extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:management.ClanMessage)
    ClanMessageOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ClanMessage.newBuilder() to construct.
  private ClanMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ClanMessage() {
    sender_ = "";
    clanName_ = "";
    message_ = "";
  }

  @Override
  @SuppressWarnings({"unused"})
  protected Object newInstance(
      UnusedPrivateParameter unused) {
    return new ClanMessage();
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ClanMessage(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            com.google.protobuf.ByteString bs = input.readBytes();
            bitField0_ |= 0x00000001;
            sender_ = bs;
            break;
          }
          case 18: {
            com.google.protobuf.ByteString bs = input.readBytes();
            bitField0_ |= 0x00000002;
            clanName_ = bs;
            break;
          }
          case 26: {
            com.google.protobuf.ByteString bs = input.readBytes();
            bitField0_ |= 0x00000004;
            message_ = bs;
            break;
          }
          case 32: {
            bitField0_ |= 0x00000008;
            rank_ = input.readInt32();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return ManagementProtos.internal_static_management_ClanMessage_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return ManagementProtos.internal_static_management_ClanMessage_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            ClanMessage.class, Builder.class);
  }

  private int bitField0_;
  public static final int SENDER_FIELD_NUMBER = 1;
  private volatile Object sender_;
  /**
   * <code>required string sender = 1;</code>
   * @return Whether the sender field is set.
   */
  @Override
  public boolean hasSender() {
    return ((bitField0_ & 0x00000001) != 0);
  }
  /**
   * <code>required string sender = 1;</code>
   * @return The sender.
   */
  @Override
  public String getSender() {
    Object ref = sender_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8()) {
        sender_ = s;
      }
      return s;
    }
  }
  /**
   * <code>required string sender = 1;</code>
   * @return The bytes for sender.
   */
  @Override
  public com.google.protobuf.ByteString
      getSenderBytes() {
    Object ref = sender_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      sender_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int CLANNAME_FIELD_NUMBER = 2;
  private volatile Object clanName_;
  /**
   * <code>required string clanName = 2;</code>
   * @return Whether the clanName field is set.
   */
  @Override
  public boolean hasClanName() {
    return ((bitField0_ & 0x00000002) != 0);
  }
  /**
   * <code>required string clanName = 2;</code>
   * @return The clanName.
   */
  @Override
  public String getClanName() {
    Object ref = clanName_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8()) {
        clanName_ = s;
      }
      return s;
    }
  }
  /**
   * <code>required string clanName = 2;</code>
   * @return The bytes for clanName.
   */
  @Override
  public com.google.protobuf.ByteString
      getClanNameBytes() {
    Object ref = clanName_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      clanName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int MESSAGE_FIELD_NUMBER = 3;
  private volatile Object message_;
  /**
   * <code>required string message = 3;</code>
   * @return Whether the message field is set.
   */
  @Override
  public boolean hasMessage() {
    return ((bitField0_ & 0x00000004) != 0);
  }
  /**
   * <code>required string message = 3;</code>
   * @return The message.
   */
  @Override
  public String getMessage() {
    Object ref = message_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      if (bs.isValidUtf8()) {
        message_ = s;
      }
      return s;
    }
  }
  /**
   * <code>required string message = 3;</code>
   * @return The bytes for message.
   */
  @Override
  public com.google.protobuf.ByteString
      getMessageBytes() {
    Object ref = message_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      message_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int RANK_FIELD_NUMBER = 4;
  private int rank_;
  /**
   * <code>required int32 rank = 4;</code>
   * @return Whether the rank field is set.
   */
  @Override
  public boolean hasRank() {
    return ((bitField0_ & 0x00000008) != 0);
  }
  /**
   * <code>required int32 rank = 4;</code>
   * @return The rank.
   */
  @Override
  public int getRank() {
    return rank_;
  }

  private byte memoizedIsInitialized = -1;
  @Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    if (!hasSender()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!hasClanName()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!hasMessage()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!hasRank()) {
      memoizedIsInitialized = 0;
      return false;
    }
    memoizedIsInitialized = 1;
    return true;
  }

  @Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (((bitField0_ & 0x00000001) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, sender_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 2, clanName_);
    }
    if (((bitField0_ & 0x00000004) != 0)) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, message_);
    }
    if (((bitField0_ & 0x00000008) != 0)) {
      output.writeInt32(4, rank_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, sender_);
    }
    if (((bitField0_ & 0x00000002) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, clanName_);
    }
    if (((bitField0_ & 0x00000004) != 0)) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, message_);
    }
    if (((bitField0_ & 0x00000008) != 0)) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(4, rank_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof ClanMessage)) {
      return super.equals(obj);
    }
    ClanMessage other = (ClanMessage) obj;

    if (hasSender() != other.hasSender()) return false;
    if (hasSender()) {
      if (!getSender()
          .equals(other.getSender())) return false;
    }
    if (hasClanName() != other.hasClanName()) return false;
    if (hasClanName()) {
      if (!getClanName()
          .equals(other.getClanName())) return false;
    }
    if (hasMessage() != other.hasMessage()) return false;
    if (hasMessage()) {
      if (!getMessage()
          .equals(other.getMessage())) return false;
    }
    if (hasRank() != other.hasRank()) return false;
    if (hasRank()) {
      if (getRank()
          != other.getRank()) return false;
    }
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasSender()) {
      hash = (37 * hash) + SENDER_FIELD_NUMBER;
      hash = (53 * hash) + getSender().hashCode();
    }
    if (hasClanName()) {
      hash = (37 * hash) + CLANNAME_FIELD_NUMBER;
      hash = (53 * hash) + getClanName().hashCode();
    }
    if (hasMessage()) {
      hash = (37 * hash) + MESSAGE_FIELD_NUMBER;
      hash = (53 * hash) + getMessage().hashCode();
    }
    if (hasRank()) {
      hash = (37 * hash) + RANK_FIELD_NUMBER;
      hash = (53 * hash) + getRank();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static ClanMessage parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ClanMessage parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ClanMessage parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ClanMessage parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ClanMessage parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ClanMessage parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ClanMessage parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ClanMessage parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static ClanMessage parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static ClanMessage parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static ClanMessage parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ClanMessage parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(ClanMessage prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @Override
  protected Builder newBuilderForType(
      BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code management.ClanMessage}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:management.ClanMessage)
      ClanMessageOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return ManagementProtos.internal_static_management_ClanMessage_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return ManagementProtos.internal_static_management_ClanMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ClanMessage.class, Builder.class);
    }

    // Construct using proto.management.ClanMessage.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @Override
    public Builder clear() {
      super.clear();
      sender_ = "";
      bitField0_ = (bitField0_ & ~0x00000001);
      clanName_ = "";
      bitField0_ = (bitField0_ & ~0x00000002);
      message_ = "";
      bitField0_ = (bitField0_ & ~0x00000004);
      rank_ = 0;
      bitField0_ = (bitField0_ & ~0x00000008);
      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return ManagementProtos.internal_static_management_ClanMessage_descriptor;
    }

    @Override
    public ClanMessage getDefaultInstanceForType() {
      return ClanMessage.getDefaultInstance();
    }

    @Override
    public ClanMessage build() {
      ClanMessage result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public ClanMessage buildPartial() {
      ClanMessage result = new ClanMessage(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        to_bitField0_ |= 0x00000001;
      }
      result.sender_ = sender_;
      if (((from_bitField0_ & 0x00000002) != 0)) {
        to_bitField0_ |= 0x00000002;
      }
      result.clanName_ = clanName_;
      if (((from_bitField0_ & 0x00000004) != 0)) {
        to_bitField0_ |= 0x00000004;
      }
      result.message_ = message_;
      if (((from_bitField0_ & 0x00000008) != 0)) {
        result.rank_ = rank_;
        to_bitField0_ |= 0x00000008;
      }
      result.bitField0_ = to_bitField0_;
      onBuilt();
      return result;
    }

    @Override
    public Builder clone() {
      return super.clone();
    }
    @Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    @Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    @Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof ClanMessage) {
        return mergeFrom((ClanMessage)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(ClanMessage other) {
      if (other == ClanMessage.getDefaultInstance()) return this;
      if (other.hasSender()) {
        bitField0_ |= 0x00000001;
        sender_ = other.sender_;
        onChanged();
      }
      if (other.hasClanName()) {
        bitField0_ |= 0x00000002;
        clanName_ = other.clanName_;
        onChanged();
      }
      if (other.hasMessage()) {
        bitField0_ |= 0x00000004;
        message_ = other.message_;
        onChanged();
      }
      if (other.hasRank()) {
        setRank(other.getRank());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @Override
    public final boolean isInitialized() {
      if (!hasSender()) {
        return false;
      }
      if (!hasClanName()) {
        return false;
      }
      if (!hasMessage()) {
        return false;
      }
      if (!hasRank()) {
        return false;
      }
      return true;
    }

    @Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      ClanMessage parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (ClanMessage) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private Object sender_ = "";
    /**
     * <code>required string sender = 1;</code>
     * @return Whether the sender field is set.
     */
    public boolean hasSender() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>required string sender = 1;</code>
     * @return The sender.
     */
    public String getSender() {
      Object ref = sender_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          sender_ = s;
        }
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>required string sender = 1;</code>
     * @return The bytes for sender.
     */
    public com.google.protobuf.ByteString
        getSenderBytes() {
      Object ref = sender_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        sender_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>required string sender = 1;</code>
     * @param value The sender to set.
     * @return This builder for chaining.
     */
    public Builder setSender(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
      sender_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required string sender = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearSender() {
      bitField0_ = (bitField0_ & ~0x00000001);
      sender_ = getDefaultInstance().getSender();
      onChanged();
      return this;
    }
    /**
     * <code>required string sender = 1;</code>
     * @param value The bytes for sender to set.
     * @return This builder for chaining.
     */
    public Builder setSenderBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
      sender_ = value;
      onChanged();
      return this;
    }

    private Object clanName_ = "";
    /**
     * <code>required string clanName = 2;</code>
     * @return Whether the clanName field is set.
     */
    public boolean hasClanName() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>required string clanName = 2;</code>
     * @return The clanName.
     */
    public String getClanName() {
      Object ref = clanName_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          clanName_ = s;
        }
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>required string clanName = 2;</code>
     * @return The bytes for clanName.
     */
    public com.google.protobuf.ByteString
        getClanNameBytes() {
      Object ref = clanName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        clanName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>required string clanName = 2;</code>
     * @param value The clanName to set.
     * @return This builder for chaining.
     */
    public Builder setClanName(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      clanName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required string clanName = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearClanName() {
      bitField0_ = (bitField0_ & ~0x00000002);
      clanName_ = getDefaultInstance().getClanName();
      onChanged();
      return this;
    }
    /**
     * <code>required string clanName = 2;</code>
     * @param value The bytes for clanName to set.
     * @return This builder for chaining.
     */
    public Builder setClanNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
      clanName_ = value;
      onChanged();
      return this;
    }

    private Object message_ = "";
    /**
     * <code>required string message = 3;</code>
     * @return Whether the message field is set.
     */
    public boolean hasMessage() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>required string message = 3;</code>
     * @return The message.
     */
    public String getMessage() {
      Object ref = message_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          message_ = s;
        }
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>required string message = 3;</code>
     * @return The bytes for message.
     */
    public com.google.protobuf.ByteString
        getMessageBytes() {
      Object ref = message_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        message_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>required string message = 3;</code>
     * @param value The message to set.
     * @return This builder for chaining.
     */
    public Builder setMessage(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
      message_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required string message = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearMessage() {
      bitField0_ = (bitField0_ & ~0x00000004);
      message_ = getDefaultInstance().getMessage();
      onChanged();
      return this;
    }
    /**
     * <code>required string message = 3;</code>
     * @param value The bytes for message to set.
     * @return This builder for chaining.
     */
    public Builder setMessageBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
      message_ = value;
      onChanged();
      return this;
    }

    private int rank_ ;
    /**
     * <code>required int32 rank = 4;</code>
     * @return Whether the rank field is set.
     */
    @Override
    public boolean hasRank() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <code>required int32 rank = 4;</code>
     * @return The rank.
     */
    @Override
    public int getRank() {
      return rank_;
    }
    /**
     * <code>required int32 rank = 4;</code>
     * @param value The rank to set.
     * @return This builder for chaining.
     */
    public Builder setRank(int value) {
      bitField0_ |= 0x00000008;
      rank_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>required int32 rank = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearRank() {
      bitField0_ = (bitField0_ & ~0x00000008);
      rank_ = 0;
      onChanged();
      return this;
    }
    @Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:management.ClanMessage)
  }

  // @@protoc_insertion_point(class_scope:management.ClanMessage)
  private static final ClanMessage DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new ClanMessage();
  }

  public static ClanMessage getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  @Deprecated public static final com.google.protobuf.Parser<ClanMessage>
      PARSER = new com.google.protobuf.AbstractParser<ClanMessage>() {
    @Override
    public ClanMessage parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ClanMessage(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ClanMessage> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<ClanMessage> getParserForType() {
    return PARSER;
  }

  @Override
  public ClanMessage getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

