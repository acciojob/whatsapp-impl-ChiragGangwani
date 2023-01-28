package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) {
        if(userMobile.contains(mobile))
            return null;
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        if(users.size()==2){
            Group g=new Group(users.get(1).getName(),2);
            adminMap.put(g,users.get(0));
            groupUserMap.put(g,users);
            return g;
        }
        customGroupCount++;
        Group g =new Group("Group "+customGroupCount,users.size());
        adminMap.put(g,users.get(0));
        groupUserMap.put(g,users);
        return g;
    }

    public int createMessage(String content) {
        messageId++;
        Message m=new Message(messageId,content);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) {
        if(!groupUserMap.containsKey(group))
            return -1;
        boolean flag=false;
        List <User> temp=groupUserMap.get(group);
        for(int u=0;u<temp.size();u++){
            if(temp.get(u)==sender)
                flag=true;
        }
        if(!flag)
            return -2;
        List<Message> ans = groupMessageMap.get(group);
                    ans.add(message);
                    groupMessageMap.put(group,ans);
                    senderMap.put(message, sender);
                    return ans.size();
    }

    public String changeAdmin(User approver, User user, Group group) {
        if(!groupUserMap.containsKey(group))
            return "group";
        if(adminMap.get(group)!=approver)
            return "approver";
        List<User>temp=groupUserMap.get(group);
        for(User x:temp){
            if(x==user){
                adminMap.put(group,user);
                return "SUCCESS";
            }
        }
        return "user";
    }

    public int removeUser(User user) {
        for(Group g:groupUserMap.keySet()){
            List<User>temp=groupUserMap.get(g);
            for(User u:temp){
                if(u==user && adminMap.get(g)==user){
                    return -2;
                }
                else if(u==user){
                    temp.remove(user);
                    List<Message> an = groupMessageMap.get(g);
                    for(Message m:senderMap.keySet()){
                        if(senderMap.get(m)==user) {
                            senderMap.remove(m);
                            for (Message e : an) {
                                if (e == m)
                                    an.remove(e);
                            }
                            groupMessageMap.put(g, an);
                        }
                    }
                    groupUserMap.put(g,temp);
                    return temp.size()+an.size()+senderMap.size();
                }
            }
        }
        return -1;
    }

    public String findMessage(Date start, Date end, int k) {
        List<Message>temp=new ArrayList<>();
        for(Message m: senderMap.keySet()){
            if(m.getTimestamp().after(start)&& m.getTimestamp().before(end))
            temp.add(m);
        }
        if(temp.size()<k)
            return "null";
            return temp.get(k).getContent();
    }
}
