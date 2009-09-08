// Generated by the protocol buffer compiler.  DO NOT EDIT!

#include "google/protobuf/unittest_import.pb.h"
#include <google/protobuf/descriptor.h>
#include <google/protobuf/io/coded_stream.h>
#include <google/protobuf/reflection_ops.h>
#include <google/protobuf/wire_format_inl.h>

namespace protobuf_unittest_import {

namespace {

const ::google::protobuf::Descriptor* ImportMessage_descriptor_ = NULL;
const ::google::protobuf::internal::GeneratedMessageReflection*
  ImportMessage_reflection_ = NULL;
const ::google::protobuf::EnumDescriptor* ImportEnum_descriptor_ = NULL;

}  // namespace


void protobuf_BuildDesc_google_2fprotobuf_2funittest_5fimport_2eproto_AssignGlobalDescriptors(const ::google::protobuf::FileDescriptor* file) {
  ImportMessage_descriptor_ = file->message_type(0);
  ImportMessage::default_instance_ = new ImportMessage();
  static const int ImportMessage_offsets_[1] = {
    GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(ImportMessage, d_),
  };
  ImportMessage_reflection_ =
    new ::google::protobuf::internal::GeneratedMessageReflection(
      ImportMessage_descriptor_,
      ImportMessage::default_instance_,
      ImportMessage_offsets_,
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(ImportMessage, _has_bits_[0]),
      GOOGLE_PROTOBUF_GENERATED_MESSAGE_FIELD_OFFSET(ImportMessage, _unknown_fields_),
      -1,
      ::google::protobuf::DescriptorPool::generated_pool(),
      sizeof(ImportMessage));
  ::google::protobuf::MessageFactory::InternalRegisterGeneratedMessage(
    ImportMessage_descriptor_, ImportMessage::default_instance_);
  ImportEnum_descriptor_ = file->enum_type(0);
  ImportMessage::default_instance_->InitAsDefaultInstance();
}

void protobuf_BuildDesc_google_2fprotobuf_2funittest_5fimport_2eproto() {
  static bool already_here = false;
  if (already_here) return;
  already_here = true;
  GOOGLE_PROTOBUF_VERIFY_VERSION;
  ::google::protobuf::DescriptorPool* pool =
    ::google::protobuf::DescriptorPool::internal_generated_pool();

  pool->InternalBuildGeneratedFile(
    "\n%google/protobuf/unittest_import.proto\022"
    "\030protobuf_unittest_import\"\032\n\rImportMessa"
    "ge\022\t\n\001d\030\001 \001(\005*<\n\nImportEnum\022\016\n\nIMPORT_FO"
    "O\020\007\022\016\n\nIMPORT_BAR\020\010\022\016\n\nIMPORT_BAZ\020\tB\034\n\030c"
    "om.google.protobuf.testH\001", 185,
  &protobuf_BuildDesc_google_2fprotobuf_2funittest_5fimport_2eproto_AssignGlobalDescriptors);
}

// Force BuildDescriptors() to be called at static initialization time.
struct StaticDescriptorInitializer_google_2fprotobuf_2funittest_5fimport_2eproto {
  StaticDescriptorInitializer_google_2fprotobuf_2funittest_5fimport_2eproto() {
    protobuf_BuildDesc_google_2fprotobuf_2funittest_5fimport_2eproto();
  }
} static_descriptor_initializer_google_2fprotobuf_2funittest_5fimport_2eproto_;

const ::google::protobuf::EnumDescriptor* ImportEnum_descriptor() {
  if (ImportEnum_descriptor_ == NULL) protobuf_BuildDesc_google_2fprotobuf_2funittest_5fimport_2eproto();
  return ImportEnum_descriptor_;
}
bool ImportEnum_IsValid(int value) {
  switch(value) {
    case 7:
    case 8:
    case 9:
      return true;
    default:
      return false;
  }
}


// ===================================================================


ImportMessage::ImportMessage()
  : ::google::protobuf::Message(),
    _cached_size_(0),
    d_(0) {
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
}

void ImportMessage::InitAsDefaultInstance() {}

ImportMessage::ImportMessage(const ImportMessage& from)
  : ::google::protobuf::Message(),
    _cached_size_(0),
    d_(0) {
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
  MergeFrom(from);
}

ImportMessage::~ImportMessage() {
  if (this != default_instance_) {
  }
}

const ::google::protobuf::Descriptor* ImportMessage::descriptor() {
  if (ImportMessage_descriptor_ == NULL) protobuf_BuildDesc_google_2fprotobuf_2funittest_5fimport_2eproto();
  return ImportMessage_descriptor_;
}

const ImportMessage& ImportMessage::default_instance() {
  if (default_instance_ == NULL) protobuf_BuildDesc_google_2fprotobuf_2funittest_5fimport_2eproto();
  return *default_instance_;
}

ImportMessage* ImportMessage::default_instance_ = NULL;

ImportMessage* ImportMessage::New() const {
  return new ImportMessage;
}

void ImportMessage::Clear() {
  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    d_ = 0;
  }
  ::memset(_has_bits_, 0, sizeof(_has_bits_));
  mutable_unknown_fields()->Clear();
}

bool ImportMessage::MergePartialFromCodedStream(
    ::google::protobuf::io::CodedInputStream* input) {
#define DO_(EXPRESSION) if (!(EXPRESSION)) return false
  ::google::protobuf::uint32 tag;
  while ((tag = input->ReadTag()) != 0) {
    switch (::google::protobuf::internal::WireFormat::GetTagFieldNumber(tag)) {
      // optional int32 d = 1;
      case 1: {
        if (::google::protobuf::internal::WireFormat::GetTagWireType(tag) !=
            ::google::protobuf::internal::WireFormat::WIRETYPE_VARINT) {
          goto handle_uninterpreted;
        }
        DO_(::google::protobuf::internal::WireFormat::ReadInt32(
              input, &d_));
        _set_bit(0);
        if (input->ExpectAtEnd()) return true;
        break;
      }
      
      default: {
      handle_uninterpreted:
        if (::google::protobuf::internal::WireFormat::GetTagWireType(tag) ==
            ::google::protobuf::internal::WireFormat::WIRETYPE_END_GROUP) {
          return true;
        }
        DO_(::google::protobuf::internal::WireFormat::SkipField(
              input, tag, mutable_unknown_fields()));
        break;
      }
    }
  }
  return true;
#undef DO_
}

bool ImportMessage::SerializeWithCachedSizes(
    ::google::protobuf::io::CodedOutputStream* output) const {
#define DO_(EXPRESSION) if (!(EXPRESSION)) return false
  // optional int32 d = 1;
  if (_has_bit(0)) {
    DO_(::google::protobuf::internal::WireFormat::WriteInt32(1, this->d(), output));
  }
  
  if (!unknown_fields().empty()) {
    DO_(::google::protobuf::internal::WireFormat::SerializeUnknownFields(
        unknown_fields(), output));
  }
  return true;
#undef DO_
}

int ImportMessage::ByteSize() const {
  int total_size = 0;
  
  if (_has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    // optional int32 d = 1;
    if (has_d()) {
      total_size += 1 +
        ::google::protobuf::internal::WireFormat::Int32Size(
          this->d());
    }
    
  }
  if (!unknown_fields().empty()) {
    total_size +=
      ::google::protobuf::internal::WireFormat::ComputeUnknownFieldsSize(
        unknown_fields());
  }
  _cached_size_ = total_size;
  return total_size;
}

void ImportMessage::MergeFrom(const ::google::protobuf::Message& from) {
  GOOGLE_CHECK_NE(&from, this);
  const ImportMessage* source =
    ::google::protobuf::internal::dynamic_cast_if_available<const ImportMessage*>(
      &from);
  if (source == NULL) {
    ::google::protobuf::internal::ReflectionOps::Merge(from, this);
  } else {
    MergeFrom(*source);
  }
}

void ImportMessage::MergeFrom(const ImportMessage& from) {
  GOOGLE_CHECK_NE(&from, this);
  if (from._has_bits_[0 / 32] & (0xffu << (0 % 32))) {
    if (from._has_bit(0)) {
      set_d(from.d());
    }
  }
  mutable_unknown_fields()->MergeFrom(from.unknown_fields());
}

void ImportMessage::CopyFrom(const ::google::protobuf::Message& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

void ImportMessage::CopyFrom(const ImportMessage& from) {
  if (&from == this) return;
  Clear();
  MergeFrom(from);
}

void ImportMessage::Swap(ImportMessage* other) {
  if (other != this) {
    std::swap(d_, other->d_);
    std::swap(_has_bits_[0], other->_has_bits_[0]);
    _unknown_fields_.Swap(&other->_unknown_fields_);
    std::swap(_cached_size_, other->_cached_size_);
  }
}

bool ImportMessage::IsInitialized() const {
  
  return true;
}

const ::google::protobuf::Descriptor* ImportMessage::GetDescriptor() const {
  return descriptor();
}

const ::google::protobuf::Reflection* ImportMessage::GetReflection() const {
  if (ImportMessage_reflection_ == NULL) protobuf_BuildDesc_google_2fprotobuf_2funittest_5fimport_2eproto();
  return ImportMessage_reflection_;
}

}  // namespace protobuf_unittest_import