package paradise.ccclxix.projectparadise;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LoaderAdapter extends BaseAdapter{

    private List<String> data = new ArrayList<>();
    private LayoutInflater layoutInflater;


    public LoaderAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null){
            view = layoutInflater.inflate(R.layout.row_comments, viewGroup, false);
        }

        TextView comment =  view.findViewById(R.id.comment);
        comment.setText(data.get(position));
        return view;
    }

    public void swapData(Collection<String> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }
}
