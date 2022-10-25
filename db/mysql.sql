CREATE TABLE `user`
(
    `id`   int NOT NULL AUTO_INCREMENT,
    `name` varchar(255)  NULL,
    `age`  int NULL,
    PRIMARY KEY (`id`) USING BTREE
);
INSERT INTO `user` VALUES (1, 'Java', 22);
INSERT INTO `user` VALUES (2, 'Python', 12);
INSERT INTO `user` VALUES (3, 'C#', 21);