package net.hasor.db.types;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.db.jdbc.core.CallableSqlParameter;
import net.hasor.db.jdbc.core.JdbcTemplate;
import net.hasor.db.types.handler.ClobReaderTypeHandler;
import net.hasor.db.types.handler.NClobReaderTypeHandler;
import net.hasor.db.types.handler.NStringReaderTypeHandler;
import net.hasor.db.types.handler.StringReaderTypeHandler;
import net.hasor.test.db.SingleDsModule;
import net.hasor.test.db.utils.DsUtils;
import net.hasor.utils.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StringReaderTypeTest {
    @Test
    public void testClobReaderTypeHandler_1() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_clob) values ('abcdefg');");
            List<Reader> dat = jdbcTemplate.query("select c_clob from tb_h2types where c_clob is not null limit 1;", (rs, rowNum) -> {
                return new ClobReaderTypeHandler().getResult(rs, 1);
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcdefg");
        }
    }

    @Test
    public void testClobReaderTypeHandler_2() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_clob) values ('abcdefg');");
            List<Reader> dat = jdbcTemplate.query("select c_clob from tb_h2types where c_clob is not null limit 1;", (rs, rowNum) -> {
                return new ClobReaderTypeHandler().getResult(rs, "c_clob");
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcdefg");
        }
    }

    @Test
    public void testClobReaderTypeHandler_3() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<Reader> dat = jdbcTemplate.query("select ?", ps -> {
                new ClobReaderTypeHandler().setParameter(ps, 1, new StringReader("abcedfg"), JDBCType.CLOB);
            }, (rs, rowNum) -> {
                return new ClobReaderTypeHandler().getNullableResult(rs, 1);
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcedfg");
        }
    }

    @Test
    public void testClobReaderTypeHandler_4() throws Exception {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_text;");
            jdbcTemplate.execute("create procedure proc_text(out p_out text) begin set p_out='abcdefg'; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_text(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.CLOB, new ClobReaderTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Reader;
            String body = IOUtils.readToString((Reader) objectMap.get("out"));
            assert body.equals("abcdefg");
        }
    }

    @Test
    public void testNClobReaderTypeHandler_1() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_clob) values ('abcdefg');");
            List<Reader> dat = jdbcTemplate.query("select c_clob from tb_h2types where c_clob is not null limit 1;", (rs, rowNum) -> {
                return new NClobReaderTypeHandler().getResult(rs, 1);
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcdefg");
        }
    }

    @Test
    public void testNClobReaderTypeHandler_2() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_clob) values ('abcdefg');");
            List<Reader> dat = jdbcTemplate.query("select c_clob from tb_h2types where c_clob is not null limit 1;", (rs, rowNum) -> {
                return new NClobReaderTypeHandler().getResult(rs, "c_clob");
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcdefg");
        }
    }

    @Test
    public void testNClobReaderTypeHandler_3() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            List<Reader> dat = jdbcTemplate.query("select ?", ps -> {
                new NClobReaderTypeHandler().setParameter(ps, 1, new StringReader("abcedfg"), JDBCType.CLOB);
            }, (rs, rowNum) -> {
                return new NClobReaderTypeHandler().getNullableResult(rs, 1);
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcedfg");
        }
    }

    @Test
    public void testNClobReaderTypeHandler_4() throws Exception {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_text;");
            jdbcTemplate.execute("create procedure proc_text(out p_out text) begin set p_out='abcdefg'; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_text(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.NCLOB, new NClobReaderTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Reader;
            String body = IOUtils.readToString((Reader) objectMap.get("out"));
            assert body.equals("abcdefg");
        }
    }

    @Test
    public void testStringTypeHandler_1() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_text) values ('abcdefg');");
            List<Reader> dat = jdbcTemplate.query("select c_text from tb_h2types where c_text is not null limit 1;", (rs, rowNum) -> {
                return new StringReaderTypeHandler().getResult(rs, 1);
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcdefg");
        }
    }

    @Test
    public void testStringTypeHandler_2() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_text) values ('abcdefg');");
            List<Reader> dat = jdbcTemplate.query("select c_text from tb_h2types where c_text is not null limit 1;", (rs, rowNum) -> {
                return new StringReaderTypeHandler().getResult(rs, "c_text");
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcdefg");
        }
    }

    @Test
    public void testStringTypeHandler_3() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            String dat1 = jdbcTemplate.queryForObject("select ?", String.class, "abcdefg");
            String dat2 = jdbcTemplate.queryForObject("select ?", String.class, (String) null);
            assert dat1.equals("abcdefg");
            assert dat2 == null;
            //
            List<Reader> dat = jdbcTemplate.query("select ?", ps -> {
                new StringReaderTypeHandler().setParameter(ps, 1, new StringReader("abcdefg"), JDBCType.CLOB);
            }, (rs, rowNum) -> {
                return new StringReaderTypeHandler().getNullableResult(rs, 1);
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcdefg");
        }
    }

    @Test
    public void testStringTypeHandler_4() throws Exception {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_varchar;");
            jdbcTemplate.execute("create procedure proc_varchar(out p_out varchar(10)) begin set p_out='abcdefg'; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_varchar(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.VARCHAR, new StringReaderTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Reader;
            String body = IOUtils.readToString((Reader) objectMap.get("out"));
            assert "abcdefg".equals(body);
        }
    }

    @Test
    public void testNStringTypeHandler_1() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_text) values ('abcdefg');");
            List<Reader> dat = jdbcTemplate.query("select c_text from tb_h2types where c_text is not null limit 1;", (rs, rowNum) -> {
                return new NStringReaderTypeHandler().getResult(rs, 1);
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcdefg");
        }
    }

    @Test
    public void testNStringTypeHandler_2() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            jdbcTemplate.executeUpdate("insert into tb_h2types (c_text) values ('abcdefg');");
            List<Reader> dat = jdbcTemplate.query("select c_text from tb_h2types where c_text is not null limit 1;", (rs, rowNum) -> {
                return new NStringReaderTypeHandler().getResult(rs, "c_text");
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcdefg");
        }
    }

    @Test
    public void testNStringTypeHandler_3() throws SQLException, IOException {
        try (AppContext appContext = Hasor.create().build(new SingleDsModule(true))) {
            JdbcTemplate jdbcTemplate = appContext.getInstance(JdbcTemplate.class);
            //
            String dat1 = jdbcTemplate.queryForObject("select ?", String.class, "abcdefg");
            String dat2 = jdbcTemplate.queryForObject("select ?", String.class, (String) null);
            assert dat1.equals("abcdefg");
            assert dat2 == null;
            //
            List<Reader> dat = jdbcTemplate.query("select ?", ps -> {
                new NStringReaderTypeHandler().setParameter(ps, 1, new StringReader("abcdefg"), JDBCType.CLOB);
            }, (rs, rowNum) -> {
                return new NStringReaderTypeHandler().getNullableResult(rs, 1);
            });
            String readerDat = IOUtils.toString(dat.get(0));
            assert readerDat.equals("abcdefg");
        }
    }

    @Test
    public void testNStringTypeHandler_4() throws Exception {
        try (Connection conn = DsUtils.localMySQL()) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(conn);
            jdbcTemplate.execute("drop procedure if exists proc_nvarchar;");
            jdbcTemplate.execute("create procedure proc_nvarchar(out p_out nvarchar(10)) begin set p_out='abcdefg'; end;");
            //
            Map<String, Object> objectMap = jdbcTemplate.call("{call proc_nvarchar(?)}",//
                    Collections.singletonList(CallableSqlParameter.withOutput("out", JDBCType.NVARCHAR, new NStringReaderTypeHandler())));
            //
            assert objectMap.size() == 2;
            assert objectMap.get("out") instanceof Reader;
            String body = IOUtils.readToString((Reader) objectMap.get("out"));
            assert "abcdefg".equals(body);
        }
    }
}