# TP Spring Boot — Correction Complète

## 📁 Structure du Projet

```
tp-spring-boot/
├── src/
│   ├── main/
│   │   ├── java/com/example/tp/
│   │   │   ├── TpApplication.java          ← Point d'entrée
│   │   │   ├── model/
│   │   │   │   ├── Product.java            ← Entité Produit
│   │   │   │   ├── Article.java            ← Entité Article
│   │   │   │   └── Comment.java            ← Entité Commentaire
│   │   │   ├── repository/
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── ArticleRepository.java
│   │   │   │   └── CommentRepository.java
│   │   │   ├── service/
│   │   │   │   ├── ProductService.java
│   │   │   │   └── ArticleService.java
│   │   │   ├── controller/
│   │   │   │   ├── ProductController.java  ← API REST /api/products
│   │   │   │   ├── ArticleController.java  ← API REST /api/articles
│   │   │   │   └── WebController.java      ← Interface Thymeleaf
│   │   │   └── exception/
│   │   │       ├── ResourceNotFoundException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.yml             ← Config de base
│   │       ├── application-dev.yml         ← Profil H2 (dev)
│   │       ├── application-prod.yml        ← Profil MySQL (prod)
│   │       ├── data.sql                    ← Données de test
│   │       └── templates/
│   │           ├── index.html
│   │           ├── products.html
│   │           ├── articles.html
│   │           └── article-detail.html
│   └── test/
│       └── ProductServiceTest.java
└── pom.xml
```

---

## 🔌 Endpoints de l'API REST

### Produits `/api/products`
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/products` | Lister tous les produits |
| GET | `/api/products?search=laptop` | Rechercher par nom |
| GET | `/api/products/{id}` | Récupérer par ID |
| POST | `/api/products` | Créer un produit |
| PUT | `/api/products/{id}` | Modifier un produit |
| DELETE | `/api/products/{id}` | Supprimer un produit |

### Blog `/api/articles`
| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/articles` | Lister tous les articles |
| GET | `/api/articles/{id}` | Récupérer avec commentaires |
| POST | `/api/articles` | Créer un article |
| PUT | `/api/articles/{id}` | Modifier un article |
| DELETE | `/api/articles/{id}` | Supprimer un article |
| GET | `/api/articles/{id}/comments` | Commentaires d'un article |
| POST | `/api/articles/{id}/comments` | Ajouter un commentaire |
| DELETE | `/api/articles/{id}/comments/{cid}` | Supprimer un commentaire |

### Interface Thymeleaf
| URL | Description |
|-----|-------------|
| `/` | Page d'accueil |
| `/products` | Gestion des produits |
| `/articles` | Liste des articles |
| `/articles/{id}` | Détail article + commentaires |
| `/h2-console` | Console BDD (dev seulement) |

---

## 1. 🔧 Gestion du Code avec Git

```bash
# 1. Initialiser le dépôt
git init
git add .
git commit -m "Initial commit : structure Spring Boot"

# 2. Créer les branches
git branch dev
git checkout dev

# 3. Ajouter le remote GitHub
git remote add origin https://github.com/VOTRE_USERNAME/tp-spring-boot.git

# 4. Pousser la branche dev
git push -u origin dev

# 5. Après chaque fonctionnalité
git add .
git commit -m "feat: ajout endpoint produits"
git push origin dev

# 6. Fusionner vers main (quand validé)
git checkout main
git merge dev
git push origin main
```

---

## 2. ⚙️ Gestion des Environnements

### Lancer en mode DEV (H2)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# ou
java -Dspring.profiles.active=dev -jar target/tp-spring-boot.jar
```

### Lancer en mode PROD (MySQL)
```bash
# Créer la base de données MySQL au préalable
# CREATE DATABASE tp_spring_boot;

java -Dspring.profiles.active=prod \
     -DDB_URL=jdbc:mysql://localhost:3306/tp_spring_boot \
     -DDB_USERNAME=root \
     -DDB_PASSWORD=monmotdepasse \
     -jar target/tp-spring-boot.jar
```

---

## 3. 📊 Comparatif des Hébergeurs Gratuits

| Critère | Render | Railway | Vercel | Netlify |
|---------|--------|---------|--------|---------|
| **Facilité d'utilisation** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Intégration GitHub** | ✅ Oui | ✅ Oui | ✅ Oui | ✅ Oui |
| **Support Spring Boot** | ✅ Oui (Java) | ✅ Oui (Java) | ❌ Non (frontend) | ❌ Non (frontend) |
| **Support MySQL/PostgreSQL** | ✅ PostgreSQL | ✅ MySQL/PostgreSQL | ❌ Non | ❌ Non |
| **Déploiement auto** | ✅ Oui | ✅ Oui | ✅ Oui | ✅ Oui |
| **Plan gratuit** | 750h/mois, sleep après 15min | 500h/mois, $5 crédit | Pas de backend Java | Pas de backend Java |
| **Variables d'env** | ✅ Dashboard | ✅ Dashboard | ✅ Dashboard | ✅ Dashboard |

### ✅ Choix Recommandé : **Render**

**Justification :** Render est le meilleur choix pour ce TP car :
- Il supporte nativement les applications **Java/Spring Boot** (contrairement à Vercel et Netlify qui sont dédiés au frontend)
- Il offre une **intégration GitHub directe** avec déploiement automatique depuis la branche `main`
- Il propose une **base de données PostgreSQL gratuite** (compatible avec Spring Data JPA)
- Le tableau de bord est intuitif pour configurer les **variables d'environnement** (profil prod)
- Railway est une alternative similaire mais le crédit gratuit est limité dans le temps

---

## 4. 🚀 Déploiement sur Render

### Étapes :
1. Créer un compte sur [render.com](https://render.com)
2. Nouveau service → **Web Service**
3. Connecter votre repo GitHub
4. Configurer :
   - **Environment** : Java
   - **Build Command** : `mvn clean package -DskipTests`
   - **Start Command** : `java -Dspring.profiles.active=prod -jar target/*.jar`
5. Variables d'environnement :
   ```
   DB_URL=jdbc:postgresql://...
   DB_USERNAME=...
   DB_PASSWORD=...
   SPRING_PROFILES_ACTIVE=prod
   ```
6. Le déploiement se déclenche automatiquement à chaque push sur `main`

---

## 5. 🧪 Tests Postman

### Exemple : Créer un produit
```json
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Ordinateur portable",
  "price": 899.99,
  "description": "Laptop 15 pouces, 16GB RAM"
}
```

### Exemple : Ajouter un commentaire
```json
POST http://localhost:8080/api/articles/1/comments
Content-Type: application/json

{
  "author": "Alice",
  "text": "Super article, très instructif !"
}
```
