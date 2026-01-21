-- Données de test pour la table payment
-- User 1: 2 crédits SUCCESS + 1 retrait SUCCESS
INSERT INTO payment (id, amount, user_id, wallet_id, status, category, created_at, created_by) VALUES
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 100.00, '11111111-1111-1111-1111-111111111111', 'aaaa1111-aaaa-1111-aaaa-111111111111', 'SUCCESS', 'CREDIT', NOW(), '11111111-1111-1111-1111-111111111111')
ON CONFLICT (id) DO NOTHING;

INSERT INTO payment (id, amount, user_id, wallet_id, status, category, created_at, created_by) VALUES
('a1b2c3d4-e5f6-7890-abcd-ef1234567891', 50.00, '11111111-1111-1111-1111-111111111111', 'aaaa1111-aaaa-1111-aaaa-111111111111', 'SUCCESS', 'CREDIT', NOW(), '11111111-1111-1111-1111-111111111111')
ON CONFLICT (id) DO NOTHING;

INSERT INTO payment (id, amount, user_id, wallet_id, status, category, created_at, created_by) VALUES
('a1b2c3d4-e5f6-7890-abcd-ef1234567892', -30.00, '11111111-1111-1111-1111-111111111111', 'aaaa1111-aaaa-1111-aaaa-111111111111', 'SUCCESS', 'WITHDRAWAL', NOW(), '11111111-1111-1111-1111-111111111111')
ON CONFLICT (id) DO NOTHING;

-- User 2: 1 crédit SUCCESS + 1 crédit FAIL
INSERT INTO payment (id, amount, user_id, wallet_id, status, category, created_at, created_by) VALUES
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 200.00, '22222222-2222-2222-2222-222222222222', 'bbbb2222-bbbb-2222-bbbb-222222222222', 'SUCCESS', 'CREDIT', NOW(), '22222222-2222-2222-2222-222222222222')
ON CONFLICT (id) DO NOTHING;

INSERT INTO payment (id, amount, user_id, wallet_id, status, category, created_at, created_by) VALUES
('b2c3d4e5-f6a7-8901-bcde-f12345678902', 5.00, '22222222-2222-2222-2222-222222222222', 'bbbb2222-bbbb-2222-bbbb-222222222222', 'FAIL', 'CREDIT', NOW(), '22222222-2222-2222-2222-222222222222')
ON CONFLICT (id) DO NOTHING;

-- User 3: 1 crédit INITIALIZE (en attente)
INSERT INTO payment (id, amount, user_id, wallet_id, status, category, created_at, created_by) VALUES
('c3d4e5f6-a7b8-9012-cdef-123456789012', 75.00, '33333333-3333-3333-3333-333333333333', 'cccc3333-cccc-3333-cccc-333333333333', 'INITIALIZE', 'CREDIT', NOW(), '33333333-3333-3333-3333-333333333333')
ON CONFLICT (id) DO NOTHING;
