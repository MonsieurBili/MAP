package Service;

import Domain.Message;
import Domain.User;
import ObserverGui.ObservableGui;
import ObserverGui.ObserverGui;
import Repository.Database.RepositoryMessageDb;
import util.EntityChangeEvent;
import util.EntityChangeEventType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceMessage extends ServiceEntity<Long, Message>
        implements ObservableGui<EntityChangeEvent<Message>> {

    private final RepositoryMessageDb messageRepository;
    private final List<ObserverGui<EntityChangeEvent<Message>>> observers = new ArrayList<>();

    public ServiceMessage(RepositoryMessageDb messageRepository) {
        super(messageRepository);
        this.messageRepository = messageRepository;
    }

    public Message sendMessage(User from, List<User> to, String messageText) {
        Message message = new Message(from, to, messageText, LocalDateTime.now());
        messageRepository.save(message);
        notifyObservers(new EntityChangeEvent<>(EntityChangeEventType.ADD, message));
        return message;
    }

    public Message replyToMessage(User from, List<User> to, String messageText, Message originalMessage) {
        Message reply = new Message(from, to, messageText, LocalDateTime.now(), originalMessage);
        messageRepository.save(reply);
        notifyObservers(new EntityChangeEvent<>(EntityChangeEventType.ADD, reply));
        return reply;
    }

    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }

    @Override
    public void addObserver(ObserverGui<EntityChangeEvent<Message>> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(ObserverGui<EntityChangeEvent<Message>> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(EntityChangeEvent<Message> event) {
        for (ObserverGui<EntityChangeEvent<Message>> observer : observers) {
            observer.update(event);
        }
    }
}

