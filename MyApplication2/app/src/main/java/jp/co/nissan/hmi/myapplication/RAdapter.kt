package jp.co.nissan.hmi.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast


class RAdapter(context: Context) : RecyclerView.Adapter<RAdapter.ViewHolder>() {

    var appsList: MutableList<AppInfo> = mutableListOf()

    //This is the subclass ViewHolder which simply
    //'holds the views' for us to show on each row
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var textView: TextView
        var img: ImageView

        init {
            //Finds the views from our row.xml
            textView = itemView.findViewById(R.id.text)
            img = itemView.findViewById(R.id.img) as ImageView
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            val context = view.context

            val launchIntent = context.packageManager.getLaunchIntentForPackage(
                                                appsList[position].packageName.toString()
                                        )
            context.startActivity(launchIntent)
            Toast.makeText(view.context, appsList[position].label.toString(), Toast.LENGTH_LONG).show()
        }
    }

    init {
    }

    override fun onBindViewHolder(viewHolder: RAdapter.ViewHolder, i: Int) {

        //Here we use the information in the list we created to define the views

        val appLabel = appsList[i].label.toString()
        val appPackage = appsList[i].packageName.toString()
        val appIcon = appsList[i].icon

        Log.d("aaabbbccc4", "aaaaaaaaaaa")

        val textView = viewHolder.textView
        textView.text = appLabel
        val imageView = viewHolder.img
        imageView.setImageDrawable(appIcon)
    }

    override fun getItemCount(): Int {

        //This method needs to be overridden so that Androids knows how many items
        //will be making it into the list

        return appsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RAdapter.ViewHolder {

        //This is what adds the code we've written in here to our target view
        val inflater = LayoutInflater.from(parent.context)

        val view = inflater.inflate(R.layout.row, parent, false)

        return ViewHolder(view)
    }
}
