-- V2: Seed admin user for development
-- password: admin1234! (BCrypt encoded)
INSERT INTO users (login_id, email, password_hash, name, role)
VALUES ('admin', 'admin@ecommerce.go.kr',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        'System Admin', 'ADMIN')
ON CONFLICT (login_id) DO NOTHING;
