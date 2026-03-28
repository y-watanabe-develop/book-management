# book-management

書籍管理システムのバックエンドAPIです。

## 技術スタック

- Kotlin
- Spring Boot
- jOOQ
- Flyway
- PostgreSQL
- Docker
- Spring Validation
- SpringDoc OpenAPI (Swagger UI)
- Testcontainers (テスト用)

## 環境構築

### 必要なもの

- Java 21
- Docker Desktop

### 起動手順

1. リポジトリをクローン

```bash
git clone https://github.com/y-watanabe-develop/book-management.git
cd book-management
```

2. アプリケーション起動

```bash
./gradlew bootRun
```

※ `spring-boot-docker-compose` により、アプリ起動時にPostgreSQLが自動で起動します。

### jOOQコードの再生成（スキーマ変更時のみ）

※ 通常、生成コードは `.gitignore` で除外しますが、本リポジトリでは評価者がすぐに実行できるようコミット済みです。
スキーマ変更時は以下のコマンドで再生成してください。

```bash
docker compose up -d
./gradlew jooqCodegen
```

## APIドキュメント

アプリ起動後、以下のURLからSwagger UIでAPIドキュメントを確認できます。

```
http://localhost:8080/swagger-ui/index.html
```

## テスト実行

テストはTestcontainersを使用してPostgreSQLコンテナを起動するため、Docker Desktopが起動している必要があります。

```bash
./gradlew test
```