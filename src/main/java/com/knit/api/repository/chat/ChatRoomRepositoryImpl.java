package com.knit.api.repository.chat;

import com.knit.api.domain.chat.ChatRoom;
import com.knit.api.domain.chat.QChatMessage;
import com.knit.api.domain.chat.QChatRoom;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ChatRoom> findActiveRoomsWithLastMessage(Long userId) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        
        return queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.user1).fetchJoin()
                .leftJoin(chatRoom.user2).fetchJoin()
                .where(
                        chatRoom.isActive.eq(true),
                        isUserParticipant(userId, chatRoom)
                )
                .orderBy(chatRoom.updatedAt.desc())
                .fetch();
    }

    @Override
    public Page<ChatRoom> findActiveRoomsWithPaging(Long userId, Pageable pageable) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        
        JPAQuery<ChatRoom> query = queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.user1).fetchJoin()
                .leftJoin(chatRoom.user2).fetchJoin()
                .where(
                        chatRoom.isActive.eq(true),
                        isUserParticipant(userId, chatRoom)
                )
                .orderBy(chatRoom.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<ChatRoom> results = query.fetch();
        
        Long count = queryFactory
                .select(chatRoom.count())
                .from(chatRoom)
                .where(
                        chatRoom.isActive.eq(true),
                        isUserParticipant(userId, chatRoom)
                )
                .fetchOne();

        return new PageImpl<>(results, pageable, count != null ? count : 0);
    }

    @Override
    public List<ChatRoom> findRoomsWithUnreadCount(Long userId) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        QChatMessage chatMessage = QChatMessage.chatMessage;
        
        return queryFactory
                .selectFrom(chatRoom)
                .leftJoin(chatRoom.user1).fetchJoin()
                .leftJoin(chatRoom.user2).fetchJoin()
                .leftJoin(chatMessage).on(chatMessage.chatRoom.eq(chatRoom))
                .where(
                        chatRoom.isActive.eq(true),
                        isUserParticipant(userId, chatRoom)
                )
                .groupBy(chatRoom.id)
                .orderBy(chatRoom.updatedAt.desc())
                .fetch();
    }

    private BooleanExpression isUserParticipant(Long userId, QChatRoom chatRoom) {
        return chatRoom.user1.id.eq(userId)
                .or(chatRoom.user2.id.eq(userId));
    }
}