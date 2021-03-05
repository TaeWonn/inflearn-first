package com.example.inflearnfirst.repository;

import com.example.inflearnfirst.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JdbcMemberRepository implements MemberRepository {

    private final DataSource dataSource;

    @Override
    public Member save(Member member) {
        String sql = "insert into MEMBER(name) values(?)";

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pst.setString(1, member.getName());

            pst.executeUpdate();
            rs = pst.getGeneratedKeys();

            if(rs.next()) {
                member.setId(rs.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pst, rs);
        }
        return null;
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from MEMBER where id = ?";

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pst = conn.prepareStatement(sql);
            pst.setLong(1, id);
            rs = pst.executeQuery();

            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public Optional<Member> findByName(String name) {
        String sql = "select * from MEMBER where name = ?";
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pst = conn.prepareStatement(sql);

            pst.setString(1, name);
            rs = pst.executeQuery();

            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                return Optional.of(member);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public List<Member> findAll() {
        String sql = "select * from member";

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

         try {
             conn = getConnection();
             pst = conn.prepareStatement(sql);

             rs = pst.executeQuery();

             List<Member> members = new ArrayList<>();
             while(rs.next()) {
                 Member member = new Member();
                 member.setId(rs.getLong("id"));
                 member.setName(rs.getString("name"));
                 members.add(member);
             }
         } catch (SQLException e) {
             throw new IllegalStateException(e);
         } finally {
             close(conn, pst, rs);
         }
        return null;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }
    private void close(Connection conn, PreparedStatement pst, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pst != null) {
                pst.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                close(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}
