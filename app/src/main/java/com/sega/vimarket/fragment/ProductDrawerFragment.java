package com.sega.vimarket.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.Toolbar.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;
import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.activity.IntroActivity;
import com.sega.vimarket.activity.LoginActivity;
import com.sega.vimarket.activity.ManagementUser;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.model.User;
import com.sega.vimarket.provider.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProductDrawerFragment extends Fragment implements OnMenuItemClickListener {

    public static User userobj;
    private Fragment fragment;
    private Unbinder unbinder;
    private SharedPreferences preferences;
    private SQLiteHandler db;
    private SessionManager session;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.catefilter)
    Spinner catefilter;
    @BindView(R.id.areafilter)
    Spinner areafilter;
    @BindView(R.id.filter)
    Spinner filter;
//    private SpaceNavigationView spaceNavigationView;
    public String category="0",area="",fitter="0",search = "";

    protected SearchView mSearchView = null;
    private SearchHistoryTable mHistoryDatabase;


    public static boolean money=false;
    // Fragment lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_drawer, container, false);
        unbinder = ButterKnife.bind(this, v);
        preferences = getContext().getSharedPreferences(ViMarket.TABLE_USER, Context.MODE_PRIVATE);
        // Setup toolbar

        toolbar.inflateMenu(R.menu.menu_product);
        toolbar.setOnMenuItemClickListener(this);
        onRefreshToolbarMenu();
        session = new SessionManager(getActivity());
        int id = session.getLoginId();

        mSearchView = (SearchView) v.findViewById(R.id.searchView);
        setSearchView();

        customSearchView();
        db = new SQLiteHandler(getActivity());

        HashMap<String, String> usermap = db.getUserDetails(id);

        userobj = new User(Integer.parseInt(usermap.get("userid")), usermap.get("username"), usermap.get("email"), usermap.get("phonenumber")
                , usermap.get("address"), usermap.get("area"), usermap.get("userpic"), usermap.get("datecreate"), usermap.get("rate"), usermap.get("count"));
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                                                                              R.array.danhmucfilter, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        // Apply the adapter to the spinner
        catefilter.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                                                                              R.array.areafilter, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        // Apply the adapter to the spinner
        areafilter.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(),
                                                                              R.array.filter, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter3.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        // Apply the adapter to the spinner
        filter.setAdapter(adapter3);
        catefilter.setSelection(0);

        // Post to avoid initial invocation
        catefilter.post(new Runnable() {
            @Override public void run() {
                catefilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Only called when the user changes the selection
                        category = position+"";
                        setSelectedDrawerItem();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
        areafilter.setSelection(0);

        // Post to avoid initial invocation
        areafilter.post(new Runnable() {
            @Override public void run() {
                areafilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Only called when the user changes the selection
                        if(position == 0){
                            area = "";
                        }
                        else {
                            area = parent.getItemAtPosition(position)+"";

                        }

                            setSelectedDrawerItem();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });

        filter.setSelection(0);

        // Post to avoid initial invocation
        filter.post(new Runnable() {
            @Override public void run() {
                filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Only called when the user changes the selection

                            fitter = position+"";



                        setSelectedDrawerItem();


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });



        if (savedInstanceState == null) {
            setSelectedDrawerItem();
        }
        else {
            fragment = getActivity().getSupportFragmentManager().findFragmentByTag(ViMarket.TAG_GRID_FRAGMENT);
            if (savedInstanceState.containsKey(ViMarket.TOOLBAR_TITLE)) {
                toolbar.setTitle(savedInstanceState.getString(ViMarket.TOOLBAR_TITLE));
            }

        }





        return v;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Toolbar action menu
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                mSearchView.open(true, item);

                return true;
            case R.id.action_grid:
                SharedPreferences.Editor editor1 = preferences.edit();
                editor1.putInt(ViMarket.VIEW_MODE, ViMarket.VIEW_MODE_GRID);
                editor1.apply();
                onRefreshToolbarMenu();
                onRefreshFragmentLayout();
                return true;
            case R.id.action_list:
                SharedPreferences.Editor editor2 = preferences.edit();
                editor2.putInt(ViMarket.VIEW_MODE, ViMarket.VIEW_MODE_LIST);
                editor2.apply();
                onRefreshToolbarMenu();
                onRefreshFragmentLayout();
                return true;
            case R.id.action_compact:
                SharedPreferences.Editor editor3 = preferences.edit();
                editor3.putInt(ViMarket.VIEW_MODE, ViMarket.VIEW_MODE_COMPACT);
                editor3.apply();
                onRefreshToolbarMenu();
                onRefreshFragmentLayout();
                return true;
            case R.id.myaccount:
                Intent backupIntent = new Intent(getContext(), ManagementUser.class);
                startActivity(backupIntent);
                return true;
            case R.id.actionabout:
                Intent rateIntent = new Intent(getActivity(), IntroActivity.class);
                startActivity(rateIntent);
                return true;
            case R.id.support:
                //                Intent supportIntent = new Intent();
                //                startActivity(supportIntent);
                return true;
            case R.id.logout:
                session.deleteLogin();
                db.deleteUsers();
                // Launching the login activity
                getActivity().getSharedPreferences(AppConfig.SHARED_PREF, Context.MODE_PRIVATE).edit().clear().apply();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                return true;
            default:
                return false;
        }
    }



    private void onRefreshToolbarMenu() {
        int viewMode = preferences.getInt(ViMarket.VIEW_MODE, ViMarket.VIEW_MODE_GRID);
        if (viewMode == ViMarket.VIEW_MODE_GRID) {
            // Change from grid to list
            Menu menu = toolbar.getMenu();
            menu.findItem(R.id.action_grid).setVisible(false);
            menu.findItem(R.id.action_list).setVisible(true);
            menu.findItem(R.id.action_compact).setVisible(false);
        }
        else if (viewMode == ViMarket.VIEW_MODE_LIST) {
            // Change from list to compact
            Menu menu = toolbar.getMenu();
            menu.findItem(R.id.action_grid).setVisible(false);
            menu.findItem(R.id.action_list).setVisible(false);
            menu.findItem(R.id.action_compact).setVisible(true);
        }
        else {
            // Change from compact to grid
            Menu menu = toolbar.getMenu();
            menu.findItem(R.id.action_grid).setVisible(true);
            menu.findItem(R.id.action_list).setVisible(false);
            menu.findItem(R.id.action_compact).setVisible(false);
        }
    }

    private void onRefreshFragmentLayout() {
        if (fragment instanceof ProductListFragment) {
            ((ProductListFragment) fragment).refreshLayout();
        } /*else if (fragment instanceof productSavedFragment) {
            ((productSavedFragment) fragment).refreshLayout();
        }*/
    }

    // Drawer item selection



  private void setSelectedDrawerItem() {


      // Create and setup bundle args
      Bundle args = new Bundle();

      args.putInt(ViMarket.VIEW_TYPE, ViMarket.VIEW_TYPE_NEW);

      args.putString(ViMarket.search_text, search);
      args.putString(ViMarket.cate_text, category);
      args.putString(ViMarket.area_text, area);
      args.putString(ViMarket.fitter_text, fitter);
      fragment = new ProductListFragment();


      fragment.setArguments(args);
      FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.content_frame, fragment, ViMarket.TAG_GRID_FRAGMENT);
      transaction.commitAllowingStateLoss();
      // Save selected position to preference
      SharedPreferences.Editor editor = preferences.edit();

      editor.apply();

  }
    protected void setSearchView() {
        mHistoryDatabase = new SearchHistoryTable(getActivity());


        if (mSearchView != null) {
            mSearchView.setVersion(SearchView.VERSION_TOOLBAR);
            mSearchView.setVersionMargins(SearchView.VERSION_MARGINS_TOOLBAR_BIG);
            mSearchView.setHint("search");
            mSearchView.setTextSize(16);
            mSearchView.setHint("Search");
            mSearchView.setDivider(false);
            mSearchView.setVoice(true);
            mSearchView.setVoiceText("Set permission on Android 6+ !");
            mSearchView.setAnimationDuration(SearchView.ANIMATION_DURATION);
            mSearchView.setShadowColor(ContextCompat.getColor(getActivity(), R.color.search_shadow_layout));
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    getData(query);
                    // mSearchView.close(false);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            mSearchView.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
                @Override
                public void onOpen() {

                }

                @Override
                public void onClose() {

                }
            });

            if (mSearchView.getAdapter() == null) {
                List<SearchItem> suggestionsList = new ArrayList<>();
//                suggestionsList.add(new SearchItem("search1"));
//                suggestionsList.add(new SearchItem("search2"));
//                suggestionsList.add(new SearchItem("search3"));

                SearchAdapter searchAdapter = new SearchAdapter(getActivity(), suggestionsList);
                searchAdapter.addOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                       search = textView.getText().toString();
                        getData(search);
                        // mSearchView.close(false);
                    }
                });
                mSearchView.setAdapter(searchAdapter);
            }

            /*
            List<SearchFilter> filter = new ArrayList<>();
            filter.add(new SearchFilter("Filter1", true));
            filter.add(new SearchFilter("Filter2", true));
            mSearchView.setFilters(filter);
            //use mSearchView.getFiltersStates() to consider filter when performing search
            */
        }
    }
    protected void customSearchView() {


            mSearchView.setVersion( SearchView.VERSION_MENU_ITEM);
            mSearchView.setVersionMargins(SearchView.VERSION_MARGINS_MENU_ITEM);
            mSearchView.setTheme(SearchView.THEME_LIGHT, true);
//            mSearchView.setTextInput("ok");

    }
    @CallSuper
   protected void getData(String text) {
       mHistoryDatabase.addItem(new SearchItem(text));
        search = text;
        setSelectedDrawerItem();
        toolbar.setTitle(text);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.action_home));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = "";
                setSelectedDrawerItem();
                toolbar.setTitle("");
                toolbar.setNavigationIcon(null);

            }
        });

        mSearchView.close(true);

   }
}
