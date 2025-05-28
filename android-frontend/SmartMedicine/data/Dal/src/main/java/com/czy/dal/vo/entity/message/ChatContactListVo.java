package com.czy.dal.vo.entity.message;


import androidx.lifecycle.MutableLiveData;

import com.czy.dal.ao.chat.ChatContactItemAo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// 和MySQL表一样，建立索引
public class ChatContactListVo {

    // RecyclerView的Vo LiveData
    public final MutableLiveData<List<ChatContactItemAo>> chatContactListLd = new MutableLiveData<>(new ArrayList<>());

    public final ConcurrentHashMap<String, Integer> contactIndex = new ConcurrentHashMap<>(new HashMap<>());

    /**
     * 以下代码待完善，先学习数据结构
     */
    // add item
    public synchronized void addContact(ChatContactItemAo newContact) {
        List<ChatContactItemAo> currentList = chatContactListLd.getValue();
        if (currentList != null) {
            currentList.add(newContact);
            int index = currentList.size() - 1; // 新联系人的索引
            contactIndex.put(newContact.contactAccount, index);
            chatContactListLd.postValue(currentList); // 更新 LiveData
        }
    }

    // remove item
    public synchronized void removeContact(String contactAccount) {
        Integer index = contactIndex.remove(contactAccount);
        if (index != null) {
            List<ChatContactItemAo> currentList = chatContactListLd.getValue();
            if (currentList != null) {
                currentList.remove((int) index);
                // 更新索引
                updateIndices(index);
                chatContactListLd.postValue(currentList); // 更新 LiveData
            }
        }
    }

    // update item
    public synchronized void updateContact(String contactAccount, ChatContactItemAo updatedContact) {
        Integer index = contactIndex.get(contactAccount);
        List<ChatContactItemAo> currentList = chatContactListLd.getValue();
        if (index != null && currentList != null) {
            currentList.set(index, updatedContact);
            chatContactListLd.postValue(currentList); // 更新 LiveData
        }
    }

    // get item
    public synchronized ChatContactItemAo findContactByAccount(String account) {
        Integer index = contactIndex.get(account);
        List<ChatContactItemAo> currentList = chatContactListLd.getValue();
        if (index != null && currentList != null) {
            return currentList.get(index);
        }
        return null;
    }

    // add items
    public synchronized void addContacts(List<ChatContactItemAo> newContacts) {
        List<ChatContactItemAo> currentList = chatContactListLd.getValue();
        if (currentList != null) {
            for (ChatContactItemAo newContact : newContacts) {
                currentList.add(newContact);
                int index = currentList.size() - 1; // 新联系人的索引
                contactIndex.put(newContact.contactAccount, index);
            }
            chatContactListLd.postValue(currentList); // 更新 LiveData
        }
    }

    // remove items
    public synchronized void removeContacts(List<String> contactAccounts) {
        List<ChatContactItemAo> currentList = chatContactListLd.getValue();
        if (currentList != null) {
            for (String account : contactAccounts) {
                Integer index = contactIndex.remove(account);
                if (index != null) {
                    currentList.remove((int) index);
                }
            }
            // 更新索引
            updateIndices(-1); // -1 表示移除多个后更新所有索引
            chatContactListLd.postValue(currentList); // 更新 LiveData
        }
    }

    // update items
    public synchronized void updateContacts(List<ChatContactItemAo> updatedContacts) {
        List<ChatContactItemAo> currentList = chatContactListLd.getValue();
        if (currentList != null) {
            for (ChatContactItemAo updatedContact : updatedContacts) {
                Integer index = contactIndex.get(updatedContact.contactAccount);
                if (index != null) {
                    currentList.set(index, updatedContact);
                }
            }
            chatContactListLd.postValue(currentList); // 更新 LiveData
        }
    }

    // get items
    public synchronized List<ChatContactItemAo> findContactsByAccounts(List<String> accounts) {
        List<ChatContactItemAo> results = new ArrayList<>();
        List<ChatContactItemAo> currentList = chatContactListLd.getValue();
        if (currentList != null) {
            for (String account : accounts) {
                Integer index = contactIndex.get(account);
                if (index != null) {
                    results.add(currentList.get(index));
                }
            }
        }
        return results;
    }

    // 更新索引
    private synchronized void updateIndices(int removedIndex) {
        List<ChatContactItemAo> currentList = chatContactListLd.getValue();
        if (currentList != null) {
            for (int i = Math.max(removedIndex, 0); i < currentList.size(); i++) {
                ChatContactItemAo item = currentList.get(i);
                contactIndex.put(item.contactAccount, i);
            }
        }
    }
}
