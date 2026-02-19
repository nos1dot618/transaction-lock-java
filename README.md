# DB Transaction-based Lock for Java

Simple **database-backed locking mechanism** using JDBC transactions.
Works across threads, processes, and multiple application instances by relying on **row-level locks**.
Supported databases:

1. **Microsoft SQL Server** (example/test included)
2. **Oracle** (`SELECT ... FOR UPDATE`)
3. **PostgreSQL** (`SELECT ... FOR UPDATE`)

## How it works?

1. Thread starts transaction (`autoCommit=false`).
2. Executes:

### SQL Server

```sql
SELECT * FROM TransactionLock
WITH (ROWLOCK, UPDLOCK, HOLDLOCK)
WHERE Purpose=?
```

### Oracle / PostgreSQL

```sql
SELECT * FROM TransactionLock
WHERE Purpose=?
FOR UPDATE
```

3. Lock held until `commit()` or `rollback()`.

## Contention test

The repository includes a stress test with:

1. 20 simultaneous threads.
2. Coordinated simultaneous start (`CountDownLatch`).
3. Artificial wait inside lock.
4. High repetition cycles.
5. High contention environment.

> Also includes a **chunk-based sequence allocator** protected by this lock.

Expected result:

1. No duplicate sequence allocations.
2. Serialized lock acquisition.
3. Increasing `CustomSequence.Seq`.
4. Correct final row count in `ItemData`.

## MSSQL setup script

Run on SQL Server (example uses `tempdb` database):

```sql
CREATE TABLE TransactionLock (
    Purpose NVARCHAR(64) PRIMARY KEY,
);

INSERT INTO TransactionLock VALUES ('lock_test');

CREATE TABLE CustomSequence (
    Id INT PRIMARY KEY IDENTITY(1,1),
    Seq INT DEFAULT 1
);

INSERT INTO CustomSequence DEFAULT VALUES;

CREATE TABLE ItemData (
    PartitionId INT NOT NULL,
    ItemId INT NOT NULL,
    CONSTRAINT PK_ItemData PRIMARY KEY (PartitionId, ItemId)
);
```

> **P.S.:** The test will take long, as it involves 20 threads, each cycling for 100 iterations, with 100 item
> insertions per cycle.

## Verify after test

```sql
SELECT * FROM CustomSequence;
SELECT COUNT(*) FROM ItemData;
```

`Seq` should equal:

```
threads * cycles * chunk_size
```

and `ItemData` count should match.
