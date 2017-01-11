package com.sega.vimarket.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

import com.sega.vimarket.R;
import com.sega.vimarket.ViMarket;
import com.sega.vimarket.activity.LoginActivity;
import com.sega.vimarket.activity.MessengerActivity;
import com.sega.vimarket.activity.PreferenceActivity;
import com.sega.vimarket.activity.ProductActivity;
import com.sega.vimarket.activity.ProfilePage;
import com.sega.vimarket.color.Colorful;
import com.sega.vimarket.config.AppConfig;
import com.sega.vimarket.config.SessionManager;
import com.sega.vimarket.provider.SQLiteHandler;
import com.sega.vimarket.search.SearchAdapter;
import com.sega.vimarket.search.SearchHistoryTable;
import com.sega.vimarket.search.SearchItem;
import com.sega.vimarket.search.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sega.vimarket.search.SearchView.SPEECH_REQUEST_CODE;

public class ProductDrawerFragment extends Fragment implements OnMenuItemClickListener {


    private Fragment fragment;
    private Unbinder unbinder;
    private SharedPreferences preferences;
    private SQLiteHandler db;
    private SessionManager session;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Boolean isTablet;
    Spinner catefilter;
    @BindView(R.id.areafilter)
    Spinner areafilter;
    @BindView(R.id.filter)
    Spinner filter;
    int areaposition;
    public String category="0",area="",fitter="0",search = "";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    protected SearchView mSearchView = null;
    private SearchHistoryTable mHistoryDatabase;


    public static boolean money=false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_product_drawer, container, false);
        unbinder = ButterKnife.bind(this, v);
        preferences = getContext().getSharedPreferences(ViMarket.TABLE_USER, Context.MODE_PRIVATE);
        // Setup toolbar
        isTablet =  getResources().getBoolean(R.bool.is_tablet);
        toolbar.inflateMenu(R.menu.menu_product);
        toolbar.setOnMenuItemClickListener(this);
        onRefreshToolbarMenu();
        session = new SessionManager(getActivity());
        int id = session.getLoginId();
        catefilter = (Spinner)v.findViewById(R.id.catefilter);
        areafilter = (Spinner)v.findViewById(R.id.areafilter);
        filter = (Spinner)v.findViewById(R.id.filter);
        mSearchView = (SearchView) v.findViewById(R.id.searchView);
        setSearchView();

      /*  final TransitionDrawable transition = new TransitionDrawable(new ColorDrawable[]{
                new ColorDrawable(Color.WHITE), new ColorDrawable(Color.BLUE)
        });

        toolbar.setBackground(transition);
        transition.startTransition(300);*/
        customSearchView();
        db = new SQLiteHandler(getActivity());

        HashMap<String, String> usermap = db.getUserDetails(id);

/*        userobj = new User(Integer.parseInt(usermap.get("userid")), usermap.get("username"), usermap.get("email"), usermap.get("phonenumber")
                , usermap.get("address"), usermap.get("area"), usermap.get("userpic"), usermap.get("datecreate"), usermap.get("rate"), usermap.get("count"));*/
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

        if(savedInstanceState!=null) {
            category = savedInstanceState.getInt("category")+"";
            catefilter.setSelection(savedInstanceState.getInt("category"), false);
        }
        else catefilter.setSelection(0,false);

        // Post to avoid initial invocation

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
        if(savedInstanceState!=null) {
            areaposition = savedInstanceState.getInt("area");
            areafilter.setSelection(areaposition, false);
        }
        else areafilter.setSelection(0,false);
        // Post to avoid initial invocation

                areafilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Only called when the user changes the selection
                        if(position == 0){
                            area = "";
                            areaposition = 0;
                        }
                        else {
                            areaposition=position;
                            area = parent.getItemAtPosition(position)+"";

                        }

                            setSelectedDrawerItem();




                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        if(savedInstanceState!=null)
        {   fitter = savedInstanceState.getInt("filter")+"";
            filter.setSelection( savedInstanceState.getInt("filter"),false);
        }

        else filter.setSelection(0,false);

        // Post to avoid initial invocation
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




        if (savedInstanceState == null) {
            setSelectedDrawerItem();
        }
        else {
            fragment = getActivity().getSupportFragmentManager().findFragmentByTag(ViMarket.TAG_GRID_FRAGMENT);
            if(!savedInstanceState.getString(ViMarket.search_text).equals(""))
            {
                toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.action_home));
                toolbar.setTitle(savedInstanceState.getString(ViMarket.search_text));
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toolbar.setTitle("");
                        toolbar.setNavigationIcon(null);
                        setSelectedDrawerItem();
                    }
                });
            }



        }





        return v;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        System.out.println(search);
        outState.putString(ViMarket.search_text, search);
        outState.putInt("category", Integer.parseInt(category));
        outState.putInt("area", areaposition);
        outState.putInt("filter", Integer.parseInt(fitter));
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
//                Intent profile =  new Intent(getActivity(), ManagementUser.class);
//                profile.putExtra(ViMarket.user_ID, session.getLoginId()+"");
//                startActivity(profile);
//                return true;
                Intent profile =  new Intent(getActivity(), ProfilePage.class);
                profile.putExtra(ViMarket.user_ID, session.getLoginId()+"");
                startActivity(profile);
                return true;
            case R.id.myinbox:
                Intent inboxIntent = new Intent(getContext(), MessengerActivity.class);
                inboxIntent.putExtra(ViMarket.user_ID, session.getLoginId()+"");
                startActivity(inboxIntent);
                return true;
            case R.id.setting:
                if(isTablet)
                    ((ProductActivity) getActivity()).loadSettingFragment();
                else
                {
                    Intent rateIntent = new Intent(getActivity(), PreferenceActivity.class);
                    startActivity(rateIntent);
                }

                return true;
            case R.id.logout:
                session.deleteLogin();
                db.deleteUsers();
                // Launching the login activity
                Colorful.config(getActivity())
                        .primaryColor(Colorful.ThemeColor.RED)
                        .accentColor(Colorful.ThemeColor.DEEP_ORANGE)
                        .translucent(false)
                        .dark(false)
                        .apply();
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

    String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
           };

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getActivity(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
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
            mSearchView.setOnVoiceClickListener(new SearchView.OnVoiceClickListener() {
                @Override
                public void onVoiceClick() {
                    if (checkPermissions()) {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");
                        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

                        if (getActivity() != null) {
                            getActivity().startActivityForResult(intent, SPEECH_REQUEST_CODE);
                        }
                    }


                }
            });
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
