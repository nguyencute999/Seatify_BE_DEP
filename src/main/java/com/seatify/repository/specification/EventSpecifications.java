package com.seatify.repository.specification;

import com.seatify.model.Event;
import com.seatify.model.constants.EventStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class EventSpecifications {

    public static Specification<Event> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("eventName")), "%" + name.toLowerCase().trim() + "%");
        };
    }

    public static Specification<Event> hasStatus(EventStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public enum TimeFilter { ALL, ONGOING, UPCOMING, FINISHED }

    public static Specification<Event> timeFilter(TimeFilter filter) {
        return (root, query, cb) -> {
            if (filter == null || filter == TimeFilter.ALL) {
                return cb.conjunction();
            }
            LocalDateTime now = LocalDateTime.now();
            switch (filter) {
                case ONGOING:
                    return cb.and(
                            cb.lessThanOrEqualTo(root.get("startTime"), now),
                            cb.greaterThanOrEqualTo(root.get("endTime"), now)
                    );
                case UPCOMING:
                    return cb.greaterThan(root.get("startTime"), now);
                case FINISHED:
                    return cb.lessThan(root.get("endTime"), now);
                default:
                    return cb.conjunction();
            }
        };
    }
}


