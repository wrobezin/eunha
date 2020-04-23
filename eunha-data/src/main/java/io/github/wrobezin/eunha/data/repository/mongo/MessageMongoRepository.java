package io.github.wrobezin.eunha.data.repository.mongo;

import io.github.wrobezin.eunha.data.entity.message.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author yuan
 * @version 1.0
 * @date 2020/4/23 17:08
 */
public interface MessageMongoRepository extends MongoRepository<Message, String> {
    /**
     * 获取已读或未读消息
     *
     * @param read 是否已读
     * @return 消息
     */
    List<Message> findAllByRead(Boolean read);

    /**
     * 统计已读或未读消息
     *
     * @param read 是否已读
     * @return 消息数量
     */
    Integer countAllByRead(Boolean read);
}
