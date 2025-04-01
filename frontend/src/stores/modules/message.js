import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useMessageStore = defineStore('free-share-message', () => {
  // 状态
  const currentReceiver = ref({
    receiverId: null,
    receiverName: '',
    receiverAvatar: ''
  });

  // actions
  function setCurrentReceiver(receiver) {
    currentReceiver.value = receiver;
  }

  function clearCurrentReceiver() {
    currentReceiver.value = {
      receiverId: null,
      receiverName: '',
      receiverAvatar: ''
    };
  }

  // 联系人列表
  const contactList = ref([]);

  function setContactList(list) {
    contactList.value = list.map(item => ({
      receiverId: item.receiverId,
      receiverName: item.receiverName,
      receiverAvatar: item.receiverAvatar,
      lastMessage: item.lastMessage,
      unreadCount: item.unreadCount,
      lastMessageTime: item.lastMessageTime,
    }));
  }

  function updateContactMessage(receiverId, message, messageType = 'text') {
    const contact = contactList.value.find(item => item.receiverId === receiverId);
    if (contact) {
      contact.lastMessage = message;
      contact.messageType = messageType;
      contact.lastMessageTime = new Date().toLocaleString();
      contact.unreadCount++;
    }
  }

  function clearUnreadCount(receiverId) {
    const contact = contactList.value.find(item => item.receiverId === receiverId);
    if (contact) {
      contact.unreadCount = 0;
    }
  }

  return {
    currentReceiver,
    setCurrentReceiver,
    clearCurrentReceiver,
    contactList,
    setContactList,
    updateContactMessage,
    clearUnreadCount
  };
});