# book-management

書籍管理システムのバックエンドAPIです。

## 技術スタック

- Kotlin
- Spring Boot
- jOOQ
- Flyway
- PostgreSQL
- Docker

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

2. jOOQコード生成

```bash
./gradlew jooqCodegen
```

3. アプリケーション起動

```bash
./gradlew bootRun
```

※ Docker Composeによりアプリ起動時にPostgreSQLが自動で起動します。

## APIドキュメント

アプリ起動後、以下のURLからSwagger UIでAPIドキュメントを確認できます。

```
http://localhost:8080/swagger-ui/index.html
```

## テスト実行

```bash
./gradlew test
```