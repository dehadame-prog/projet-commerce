-- Données initiales pour le profil DEV (H2)
-- Ce fichier est exécuté automatiquement au démarrage

INSERT INTO products (name, price, description) VALUES
    ('Ordinateur portable', 899.99, 'Laptop 15 pouces, 16GB RAM, 512GB SSD'),
    ('Souris sans fil', 29.99, 'Souris ergonomique Bluetooth'),
    ('Clavier mécanique', 79.99, 'Clavier rétroéclairé RGB');

INSERT INTO articles (title, content) VALUES
    ('Introduction à Spring Boot', 'Spring Boot simplifie le développement Java en fournissant une configuration automatique et un serveur intégré.'),
    ('Les annotations Spring', 'Les annotations comme @RestController, @Service et @Repository permettent de définir le rôle de chaque classe dans l''architecture.');

INSERT INTO comments (text, author, article_id) VALUES
    ('Super article, très clair !', 'Alice', 1),
    ('Merci pour cette explication détaillée.', 'Bob', 1),
    ('J''utilise ces annotations tous les jours !', 'Charlie', 2);
