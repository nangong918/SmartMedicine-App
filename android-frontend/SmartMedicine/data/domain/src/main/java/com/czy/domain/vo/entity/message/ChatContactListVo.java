package com.czy.domain.vo.entity.message;


import com.czy.domain.ao.message.ChatContactItemAo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// 和MySQL表一样，建立索引
public class ChatContactListVo {

    // RecyclerView的Vo LiveData
    public List<ChatContactItemAo> chatContactList = new ArrayList<>();

    public final ConcurrentHashMap<String, Integer> contactIndex = new ConcurrentHashMap<>(new HashMap<>());

    /**
     * 以下代码待完善，先学习数据结构
     */
    // add item
    public synchronized void addContact(ChatContactItemAo newContact) {
        chatContactList.add(newContact);
        int index = chatContactList.size() - 1; // 新联系人的索引
        contactIndex.put(newContact.contactAccount, index);
    }

    // remove item
    public synchronized void removeContact(String contactAccount) {
        Integer index = contactIndex.remove(contactAccount);
        if (index != null) {
            chatContactList.remove((int) index);
            // 更新索引
            updateIndices(index);
        }
    }

    // update item
    public synchronized void updateContact(String contactAccount, ChatContactItemAo updatedContact) {
        Integer index = contactIndex.get(contactAccount);
        if (index != null) {
            chatContactList.set(index, updatedContact);
        }
    }

    // get item
    public synchronized ChatContactItemAo findContactByAccount(String account) {
        Integer index = contactIndex.get(account);
        if (index != null) {
            return chatContactList.get(index);
        }
        return null;
    }

    // add items
    public synchronized void addContacts(List<ChatContactItemAo> newContacts) {
        for (ChatContactItemAo newContact : newContacts) {
            chatContactList.add(newContact);
            int index = chatContactList.size() - 1; // 新联系人的索引
            contactIndex.put(newContact.contactAccount, index);
        }
    }

    // remove items
    public synchronized void removeContacts(List<String> contactAccounts) {
        for (String account : contactAccounts) {
            Integer index = contactIndex.remove(account);
            if (index != null) {
                chatContactList.remove((int) index);
            }
        }
        // 更新索引
        updateIndices(-1); // -1 表示移除多个后更新所有索引
    }

    // update items
    public synchronized void updateContacts(List<ChatContactItemAo> updatedContacts) {
        for (ChatContactItemAo updatedContact : updatedContacts) {
            Integer index = contactIndex.get(updatedContact.contactAccount);
            if (index != null) {
                chatContactList.set(index, updatedContact);
            }
        }
    }

    // get items
    public synchronized List<ChatContactItemAo> findContactsByAccounts(List<String> accounts) {
        List<ChatContactItemAo> results = new ArrayList<>();
        for (String account : accounts) {
            Integer index = contactIndex.get(account);
            if (index != null) {
                results.add(chatContactList.get(index));
            }
        }
        return results;
    }

    // 更新索引
    private synchronized void updateIndices(int removedIndex) {
        for (int i = Math.max(removedIndex, 0); i < chatContactList.size(); i++) {
            ChatContactItemAo item = chatContactList.get(i);
            contactIndex.put(item.contactAccount, i);
        }
    }
}
