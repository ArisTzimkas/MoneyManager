package com.example.money_manager.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyDao {

    @Insert
    public void addTransaction(Transactions transactions);

    @Insert
    public void addCategory(Category category);


    @Query("SELECT * FROM transactions")
    List<Transactions> getTransactions();
    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY id DESC LIMIT 1")
    Transactions getLastTransaction(String userId);

    @Query("SELECT * FROM transactions WHERE user_id = :userId")
    List<Transactions> getTransactionsByUserId(String userId);

    @Query("SELECT transactions.* FROM transactions " +
            "INNER JOIN expense_category ON transactions.cat_id = expense_category.cid " +
            "WHERE expense_category.category_name = :category " +
            "AND transactions.date BETWEEN :startDate AND :endDate " +
            "AND transactions.transaction_type = 'ΕΞΟΔΑ' " +
            "AND transactions.user_id = :user")
    List<Transactions> getTransactionsByCategoryAndDateRangeAndUser(String category, String startDate, String endDate, String user);


    @Query("SELECT ec.category_name AS categoryName, COUNT(t.id) AS transactionCount " +
            "FROM transactions t " +
            "INNER JOIN expense_category ec ON t.cat_id = ec.cid " +
            "WHERE t.user_id = :user " +
            "AND t.transaction_type = 'ΕΞΟΔΑ' " +
            "GROUP BY ec.category_name")
    List<CategoryTransactionCount> getTransactionCountByCategoryAndUser(String user);









    @Query("SELECT cid FROM expense_category WHERE category_name= :catname")
    int getCatId(String catname);

    @Query("SELECT MAX(cid) FROM expense_category")
    int getLastCategoryId();

    @Query("SELECT COUNT(*) FROM expense_category")
    int getCategoryCount();

    @Query("SELECT * FROM expense_category")
    List<Category> getCategory();

    @Query("SELECT * FROM expense_category WHERE cid = :cid LIMIT 1")
    Category getCategoryById(int cid);

    @Insert
    public void addSchedule(Schedule schedule);

    @Query("SELECT MAX(sid) FROM schedule")
    int getLastSchedule();

    @Query("SELECT * FROM schedule WHERE date = :date AND category_name = :userId")
    List<Schedule> getSchedulesForDateAndUser(String date, String userId);





    @Query("SELECT SUM(transaction_value) AS totalIncome FROM transactions WHERE transaction_type = 'ΕΣΟΔΑ' AND user_id = :userId")
    int getTotalIncome(String userId);


    @Query("SELECT SUM(transaction_value) AS totalExpenses FROM transactions WHERE transaction_type = 'ΕΞΟΔΑ' AND user_id = :userId")
    int getTotalExpenses(String userId);






    @Query("DELETE FROM transactions")
    void deleteAllFromTransactions();
}
