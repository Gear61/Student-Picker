package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.R;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class NameChoosingAdapter extends BaseAdapter {
    private String outOfNames;
    private String noNames;
    private Context context;
    private String listName;
    private List<String> names;
    private TextView noContent;
    private DataSource dataSource;

    public NameChoosingAdapter(Context context, TextView noContent, String listName) {
        this.outOfNames = context.getString(R.string.out_of_names);
        this.noNames = context.getString(R.string.no_names);
        this.context = context;
        this.listName = listName;
        this.dataSource = new DataSource(context);
        List<String> cachedNames = PreferencesManager.get().getCachedNameList(listName);
        this.names = cachedNames.isEmpty() ? dataSource.getAllNamesInList(listName) : cachedNames;
        Collections.sort(this.names);
        this.noContent = noContent;
        setNoContent();
    }

    public void addName(String name) {
        names.add(name);
        Collections.sort(names);
        notifyDataSetChanged();
        setNoContent();
    }

    public void removeName(String name) {
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).equals(name)) {
                names.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
        setNoContent();
    }

    public void changeName(String oldName, String newName) {
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).equals(oldName)) {
                names.set(i, newName);
                Collections.sort(names);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void setNoContent() {
        if (dataSource.getAllNamesInList(listName).isEmpty()) {
            noContent.setText(noNames);
        }
        else {
            noContent.setText(outOfNames);
        }
        int viewVisibility = names.isEmpty() ? View.VISIBLE : View.GONE;
        noContent.setVisibility(viewVisibility);
    }

    public String chooseNamesAtRandom(List<Integer> indexes, boolean withReplacement) {
        StringBuilder chosenNames = new StringBuilder();
        for (int i = 0; i < indexes.size(); i++) {
            if (i != 0) {
                chosenNames.append("\n");
            }
            chosenNames.append(names.get(indexes.get(i)));
        }
        // If without replacement, remove the names
        if (!withReplacement) {
            removeNamesAtPositions(indexes);
        }
        return chosenNames.toString();
    }

    public void removeNamesAtPositions(List<Integer> indexes) {
        Collections.sort(indexes);
        Collections.reverse(indexes);
        for (int index : indexes) {
            names.remove(index);
        }
        notifyDataSetChanged();
        setNoContent();
    }

    public void removeNameAtPosition(int position) {
        names.remove(position);
        notifyDataSetChanged();
        setNoContent();
    }

    public void resetNames() {
        names.clear();
        names.addAll(dataSource.getAllNamesInList(listName));
        Collections.sort(names);
        setNoContent();
        notifyDataSetChanged();
    }

    public void cacheNamesList() {
        PreferencesManager.get().cacheNameChoosingList(listName, names);
    }

    public void processListNameChange(String newListName) {
        PreferencesManager.get().moveNamesListCache(listName, newListName);
        listName = newListName;
    }

    public int getCount()
    {
        return names.size();
    }

    public String getItem(int position)
    {
        return names.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public class NameViewHolder {
        @Bind(R.id.person_name) TextView name;
        @Bind(R.id.delete_icon) IconTextView delete;

        public NameViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    // Renders the ListView item that the user has scrolled to or is about to scroll to
    public View getView(final int position, View view, ViewGroup parent) {
        NameViewHolder holder;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.choose_name_cell, parent, false);
            holder = new NameViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (NameViewHolder) view.getTag();
        }
        holder.name.setText(names.get(position));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                removeNameAtPosition(position);
            }
        });
        return view;
    }
}