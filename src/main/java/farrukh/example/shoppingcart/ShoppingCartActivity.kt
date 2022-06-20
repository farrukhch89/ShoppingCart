package farrukh.example.shoppingcart

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import java.math.BigDecimal
import java.sql.SQLException
import androidx.appcompat.app.AppCompatActivity

class ShoppingCartActivity : AppCompatActivity() {
    private lateinit var dbHelper: StoreDatabase
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)
        // connect to database and open it to read data
        dbHelper = StoreDatabase(this)
        try {
            dbHelper!!.open()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        // get total number of item
        val total: Int = dbHelper!!.getTotalItemsCount()
        // record number of count in car
        val num = dbHelper!!.getCartItemsRowCount(1)
        //get amount of item
        val amount: Int = dbHelper!!.getAmount()
        val priceVal: BigDecimal
        priceVal = if (total == num) {
            val tAmount = amount - 0.2 * amount
            BigDecimal.valueOf(tAmount.toLong(), 2)
        } else {
            BigDecimal.valueOf(amount.toLong(), 2)
        }
        // adding sum of data in cart
        val numItemsBought = findViewById(R.id.cart) as TextView
        numItemsBought.text = "$num of $total items"
        val totalAmount = findViewById(R.id.total) as TextView
        totalAmount.text = "Total Amount: €$priceVal"
        val cart = (findViewById(R.id.linearLayout) as LinearLayout?)!!
        cart.setOnClickListener { //Clean all data
            dbHelper!!.deleteAllItems()
            val intent = Intent(this@ShoppingCartActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        //Generate ListView from SQLite Database
        displayListView()
    }

    // fetching data
    private fun displayListView() {
        val cursor =
            dbHelper!!.fetchAllItems(1) // 1 is used to denote an item in the shopping cart

        // Display name of item to be bought
        val columns = arrayOf(
            StoreDatabase.KEY_NAME
        )

        // the XML defined view which the data will be bound to
        val to = intArrayOf(
            R.id.name
        )

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        val dataAdapter = SimpleCursorAdapter(
            this, R.layout.item_layout,
            cursor,
            columns,
            to,
            0
        )
        val listView = (findViewById(R.id.listView) as ListView?)!!
        listView.adapter = dataAdapter
    }
}