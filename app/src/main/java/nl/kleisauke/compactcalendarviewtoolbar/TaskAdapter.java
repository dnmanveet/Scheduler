package nl.kleisauke.compactcalendarviewtoolbar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<TaskItem>{

    private List<TaskItem> TaskList;
    Context mContext;

    public TaskAdapter(@NonNull Context context, int resource, @NonNull List<TaskItem> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.TaskList = objects;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView taskTitle;

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_todo, null);
        }

        TaskItem p  = getItem(position);
        if (p != null) {
            TextView taskTitle = (TextView) v.findViewById(R.id.task_title);
            TextView timer = (TextView) v.findViewById(R.id.timer);

            if (taskTitle != null) {
                taskTitle.setText(p.getTaskString());
                timer.setText(p.getDateString());
            }


        }

        return v;
    }

}
