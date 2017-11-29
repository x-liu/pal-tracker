package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    private HashMap<Long, TimeEntry> timeEntries = new HashMap<>();

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        timeEntry.setId(timeEntries.size() + 1);
        timeEntries.put(timeEntry.getId(), timeEntry);
        return timeEntry;
    }

    @Override
    public TimeEntry find(Long id) {
//        if (timeEntries.size() > 0) {
            return timeEntries.get(id);
//        } else {
//            return null;
//        }
    }

    @Override
    public List<TimeEntry> list() {
        return new ArrayList<>(timeEntries.values());
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
//        if (timeEntries.containsKey(id)) {
            timeEntries.replace(id, timeEntry);
            timeEntry.setId(id);
            return timeEntry;
//        } else {
//            return null;
//        }
    }

    @Override
    public void delete(Long id) {
//        if (timeEntries.containsKey(id)) {
            timeEntries.remove(id);
//        }
    }
}
