package hcmute.edu.projectfinal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import hcmute.edu.projectfinal.R;
import hcmute.edu.projectfinal.adapter.ChatTabAdapter;

public class ChatTabFragment extends Fragment {
    private int targetTabIndex = 0;
    private String majorTitle = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_tabs, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Nhận dữ liệu từ Bundle
        if (getArguments() != null) {
            targetTabIndex = getArguments().getInt("target_tab_index", 0);
            majorTitle = getArguments().getString("major_title");
        }

        // Truyền dữ liệu vào adapter nếu cần
        ChatTabAdapter tabAdapter = new ChatTabAdapter(requireActivity(), majorTitle);
        viewPager.setAdapter(tabAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Trò chuyện");
            else tab.setText("Lịch sử");
        }).attach();

        // Chuyển đến tab mục tiêu
        viewPager.setCurrentItem(targetTabIndex, false);

        return view;
    }
}