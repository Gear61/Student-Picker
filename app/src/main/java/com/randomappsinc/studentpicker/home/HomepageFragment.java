package com.randomappsinc.studentpicker.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.listpage.ListActivity;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomepageFragment extends Fragment implements
        NameListsAdapter.Delegate, RenameListDialog.Listener,
        DeleteListDialog.Listener {

    public static HomepageFragment getInstance() {
        return new HomepageFragment();
    }

    @BindView(R.id.user_lists) RecyclerView lists;
    @BindView(R.id.no_content) View noContent;

    private PreferencesManager preferencesManager;
    private DataSource dataSource;
    private NameListsAdapter nameListsAdapter;
    private RenameListDialog renameListDialog;
    private DeleteListDialog deleteListDialog;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.homepage_fragment,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferencesManager = new PreferencesManager(getContext());
        renameListDialog = new RenameListDialog(this, getContext());
        deleteListDialog = new DeleteListDialog(this, getContext());
        dataSource = new DataSource(getContext());

        nameListsAdapter = new NameListsAdapter(this, dataSource.getNameLists());
        lists.setAdapter(nameListsAdapter);
        lists.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        setNoContent();
    }

    @OnClick(R.id.create_name_list_button)
    public void createNameList() {
        ((HomeActivity) getActivity()).createNameList();
    }

    @OnClick(R.id.import_from_txt_button)
    public void importFromTextFile() {
        ((HomeActivity) getActivity()).importFromTextFile();
    }

    @Override
    public void onResume() {
        super.onResume();
        nameListsAdapter.refresh(dataSource.getNameLists());
        setNoContent();
    }

    @Override
    public void onItemClick(ListDO listDO) {
        Intent intent = new Intent(getActivity(), ListActivity.class);
        intent.putExtra(Constants.LIST_ID_KEY, listDO.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        getActivity().startActivity(intent);
    }

    @Override
    public void onItemEditClick(int position, ListDO listDO) {
        renameListDialog.show(position, listDO);
    }

    @Override
    public void onRenameListConfirmed(int position, ListDO updatedList) {
        dataSource.renameList(updatedList);
        preferencesManager.renameList(nameListsAdapter.getItem(position).getName(), updatedList.getName());
        nameListsAdapter.renameItem(position, updatedList.getName());
    }

    @Override
    public void onItemDeleteClick(int position, ListDO listDO) {
        deleteListDialog.presentForList(position, listDO);
    }

    @Override
    public void onDeleteListConfirmed(int position, ListDO listDO) {
        dataSource.deleteList(listDO.getId());
        preferencesManager.removeNameList(listDO.getName());
        nameListsAdapter.deleteItem(position);
    }

    @Override
    public void setNoContent() {
        if (nameListsAdapter.getItemCount() == 0) {
            lists.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
            lists.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
