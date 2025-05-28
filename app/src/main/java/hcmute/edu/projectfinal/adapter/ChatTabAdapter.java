package hcmute.edu.projectfinal.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import hcmute.edu.projectfinal.fragment.ChatFragment;
import hcmute.edu.projectfinal.fragment.HistoryFragment;

public class ChatTabAdapter extends FragmentStateAdapter {
    public ChatTabAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new ChatFragment();
        else return new HistoryFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}