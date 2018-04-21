package paradise.ccclxix.projectparadise.Loaders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import paradise.ccclxix.projectparadise.APIForms.Event;
import paradise.ccclxix.projectparadise.R;

public class EventLoaderAdapter extends BaseAdapter {

    private List<Event> data = new ArrayList<>();
    private LayoutInflater layoutInflater;


    public EventLoaderAdapter(Context context){
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
            view = layoutInflater.inflate(R.layout.row_events, viewGroup, false);
        }

        TextView event_name =  view.findViewById(R.id.event_name);
        TextView event_logo =  view.findViewById(R.id.event_logo);
        event_name.setText(data.get(position).getEvent_name());
        event_logo.setText(String.valueOf(data.get(position).getEvent_name().charAt(0)));
        return view;
    }

    public void swapData(Collection<Event> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }
}