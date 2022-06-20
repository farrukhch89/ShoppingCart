package farrukh.example.shoppingcart

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import java.math.BigDecimal
import java.sql.SQLException


class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: StoreDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = StoreDatabase(this)
        try {
            dbHelper!!.open()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        //check if items are available
        val total: Int = dbHelper!!.getTotalItemsCount()
        if (total <= 0) {
            //Add some data
            dbHelper!!.insertMyShopItems()
        }
        //Generate ListView from SQLite Database
        displayListView()
        val num: Int = dbHelper!!.getCartItemsRowCount(1)
        val amount: Int = dbHelper.getAmount()
        val priceVal: BigDecimal
        priceVal = if (total == num) {
            val tAmount = amount - 0.2 * amount
            BigDecimal.valueOf(tAmount.toLong(), 2)
        } else {
            BigDecimal.valueOf(amount.toLong(), 2)
        }
        val numItemsBought = findViewById(R.id.cart) as TextView
        numItemsBought.text = "$num of $total items"
        val totalAmount = findViewById(R.id.total) as TextView
        totalAmount.text = "Total Amount: €$priceVal"
        val cart = (findViewById(R.id.linearLayout) as LinearLayout)
        cart.setOnClickListener {
            val intent = Intent(this@MainActivity, ShoppingCartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayListView() {
        val cursor: Cursor? =
            dbHelper.fetchAllItems(0) // 0 is used to denote an item yet to be bought

        // Display name of item to be sold
        val columns = arrayOf<String>(
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
        val listView = (findViewById(R.id.listView) as ListView)
        listView.adapter = dataAdapter
        listView.onItemClickListener =
            OnItemClickListener { listView, view, position, id -> // Get the cursor, positioned to the corresponding row in the result set
                val cursor = listView.getItemAtPosition(position) as Cursor

                // Get the item attributes to be sent to details activity from this row in the database.
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val price = cursor.getInt(cursor.getColumnIndexOrThrow("price"))
                val itemId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"))
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("description", description)
                intent.putExtra("price", price)
                intent.putExtra("_id", itemId)
                startActivity(intent)
            }
    }
}