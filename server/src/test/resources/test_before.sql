INSERT INTO `users` (`id`, `address`, `chain`, `create_time`, `update_time`)
VALUES
	(1, 'address_1', 'BTC', '2024-08-10 17:36:15', '2024-08-10 17:36:37'),
	(2, 'address_2', 'ETH', '2024-08-10 17:36:21', '2024-08-10 17:36:39');

INSERT INTO `ordinals` (`id`, `inscription_number`, `inscription_id`, `content`, `content_body`, `status`, `create_time`, `update_time`)
VALUES
	(1, 1, 'tx_1i0', 'https://static-testnet.unisat.io/content/2a7d895cab31ec6a2a7ae44d2609acd2380429f1bbe97fade9896e0f7ebd84cai0', '{\"p\":\"brc-20\",\"op\":\"transfer\",\"tick\":\"🥚\",\"amt\":\"888\"}', 'AVAILABLE', '2024-08-10 17:38:56', '2024-08-10 17:40:05'),
	(2, 2, 'tx_2i0', 'https://static-testnet.unisat.io/content/4269c3f91c8c0410deab8348154669d427c54d902182786423658c902589b00di0', '{\"p\":\"brc-20\",\"op\":\"transfer\",\"tick\":\"🥚\",\"amt\":\"1111\"}', 'UNAVAILABLE', '2024-08-10 17:39:05', '2024-08-10 17:41:34');
