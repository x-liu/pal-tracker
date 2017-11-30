package io.pivotal.pal.tracker;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.sql.PreparedStatement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.util.List;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    //private DataSource myDataSource;
    private JdbcTemplate jdbctemp;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbctemp = new JdbcTemplate();
        this.jdbctemp.setDataSource(dataSource);
        //this.myDataSource = dataSource;
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;






    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        KeyHolder kh = new GeneratedKeyHolder();
        String sql = "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                "VALUES (?, ?, ?, ?)";
        jdbctemp.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql,
                    RETURN_GENERATED_KEYS);

            statement.setLong(1,timeEntry.getProjectId());
            statement.setLong(2,timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setLong(4,timeEntry.getHours());

            return statement;
        }, kh);


        //System.out.println("after inserting " + kh.getKey().longValue());
        return find(kh.getKey().longValue());
    }

    @Override
    public TimeEntry find(Long id) {
        return jdbctemp.query(
                "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?",
                new Object[]{id},
                extractor);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbctemp.query(
                "SELECT id, project_id, user_id, date, hours FROM time_entries",
                mapper);
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        jdbctemp.update(
                "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = ?",
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),
                id);
        return find(id);
    }

    @Override
    public void delete(Long id) {
        //System.out.print("deleting database entry " + id);
        jdbctemp.update(
                "DELETE FROM time_entries WHERE id = ?",
                id);
    }
}
