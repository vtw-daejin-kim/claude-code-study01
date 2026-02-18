package kr.go.ecommerce.global.migration;

import kr.go.ecommerce.global.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayMigrationTest extends IntegrationTestBase {

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldCreateAllTables() throws Exception {
        List<String> tables = getTableNames();
        assertThat(tables).containsExactlyInAnyOrder(
                "users", "brands", "products", "product_stock",
                "product_revisions", "likes", "cart_items",
                "orders", "order_items", "order_cart_restore"
        );
    }

    @Test
    void usersTableShouldHaveCorrectColumns() throws Exception {
        List<String> columns = getColumnNames("users");
        assertThat(columns).containsExactlyInAnyOrder(
                "id", "login_id", "email", "password_hash",
                "name", "role", "created_at", "updated_at"
        );
    }

    @Test
    void productStockShouldHaveCorrectColumns() throws Exception {
        List<String> columns = getColumnNames("product_stock");
        assertThat(columns).containsExactlyInAnyOrder(
                "product_id", "on_hand", "reserved"
        );
    }

    @Test
    void ordersTableShouldHaveCorrectColumns() throws Exception {
        List<String> columns = getColumnNames("orders");
        assertThat(columns).containsExactlyInAnyOrder(
                "id", "user_id", "status", "order_source",
                "idempotency_key", "total_amount", "expires_at",
                "created_at", "updated_at"
        );
    }

    @Test
    void orderItemsShouldHaveSnapshotColumns() throws Exception {
        List<String> columns = getColumnNames("order_items");
        assertThat(columns).contains(
                "snapshot_unit_price", "snapshot_product_name",
                "snapshot_brand_id", "snapshot_brand_name", "snapshot_image_url"
        );
    }

    @Test
    void likesUniqueConstraint() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getIndexInfo(null, null, "likes", true, false);
            List<String> uniqueIndexColumns = new ArrayList<>();
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                if (columnName != null) {
                    uniqueIndexColumns.add(columnName);
                }
            }
            assertThat(uniqueIndexColumns).contains("user_id", "product_id");
        }
    }

    @Test
    void productStockCheckConstraints() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            // Verify CHECK constraints exist by querying information_schema
            var stmt = conn.prepareStatement(
                    "SELECT constraint_name FROM information_schema.table_constraints " +
                    "WHERE table_name = 'product_stock' AND constraint_type = 'CHECK'"
            );
            ResultSet rs = stmt.executeQuery();
            List<String> checkConstraints = new ArrayList<>();
            while (rs.next()) {
                checkConstraints.add(rs.getString("constraint_name"));
            }
            assertThat(checkConstraints).contains(
                    "chk_on_hand_non_negative", "chk_reserved_non_negative"
            );
        }
    }

    private List<String> getTableNames() throws Exception {
        List<String> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, "public", null, new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!tableName.startsWith("flyway_")) {
                    tables.add(tableName);
                }
            }
        }
        return tables;
    }

    private List<String> getColumnNames(String tableName) throws Exception {
        List<String> columns = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, "public", tableName, null);
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
        }
        return columns;
    }
}
