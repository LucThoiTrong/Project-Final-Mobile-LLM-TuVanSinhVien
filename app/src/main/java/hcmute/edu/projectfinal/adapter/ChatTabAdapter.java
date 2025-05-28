package hcmute.edu.projectfinal.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import hcmute.edu.projectfinal.fragment.ChatFragment;
import hcmute.edu.projectfinal.fragment.HistoryFragment;

public class ChatTabAdapter extends FragmentStateAdapter {
    private final String majorTitle;

    public ChatTabAdapter(@NonNull FragmentActivity fragmentActivity, String majorTitle) {
        super(fragmentActivity);
        this.majorTitle = majorTitle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            ChatFragment chatFragment = new ChatFragment();
            if (majorTitle != null) {
                Bundle bundle = new Bundle();
                bundle.putString("major_title", majorTitle);
                chatFragment.setArguments(bundle);
            }
            return chatFragment;
        } else {
            return new HistoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
