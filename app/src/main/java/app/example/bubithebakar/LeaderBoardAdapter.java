package app.example.bubithebakar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import app.example.bubithebakar.R;

import java.util.List;

/**
 * This is a custom adapter that changes the strings in a listview to blue.
 */

public class LeaderBoardAdapter extends ArrayAdapter<User> {

    private final Context mContext;
    private final int id;
    private final List<User> items;
    private final User user;

    /**
     * Sets the custom adapter.
     * Works when the adapter is defined.
     *
     * @param context
     * @param textViewResourceId
     * @param list
     */
    public LeaderBoardAdapter(Context context, int textViewResourceId, List<User> list,User user) {
        super(context, textViewResourceId, list);
        mContext = context;
        id = textViewResourceId;
        items = list;
        this.user = user;
    }

    /**
     * Sets the blue color.
     * Works when the adapter is defined.
     *
     * @param position
     * @param v
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View v, ViewGroup parent) {
        View mView = v;
        if (mView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(R.id.textView);

        if(items.get(position).getUserName().equals(user.getUserName()))
        {
            text.setBackgroundColor(Color.rgb(128,128,0));
        }
        else
        {
            text.setBackgroundColor(0x00000000);
        }

        if (items.get(position).getHistory().isEmpty()) {
            items.get(position).updateHistory("לא עודכן",new DayData(0,0,0));
            items.get(position).setLastUpdatedDate("לא עודכן");
            text.setText(items.get(position).getUserName()+" | עודכן לאחרונה ב: " + items.get(position).getLastUpdatedDate() + "\n" + "בקרות: " + items.get(position).getMonthlyBakarot() + " תיקופים: " + items.get(position).getMonthlyTikufim() + " קנסות: " + items.get(position).getMonthlyKnasot());
            return mView;
        }


        text.setText(items.get(position).getUserName()+" | עודכן לאחרונה ב: " + items.get(position).getLastUpdatedDate().replace("-","/") + "\n" + "בקרות: " + items.get(position).getMonthlyBakarot() + " תיקופים: " + items.get(position).getMonthlyTikufim() + " קנסות: " + items.get(position).getMonthlyKnasot());
        text.setTextColor(Color.rgb(0,0,0));
        return mView;
    }

}