package net.glassstones.bambammusic.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.FragmentIdentityListener;
import net.glassstones.bambammusic.models.LocalCreditCard;
import net.glassstones.bambammusic.ui.adapters.CardsAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnListCardsFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ListCardsFragment extends Fragment {

    @Bind(R.id.cardList)
    RecyclerView cardList;
    private OnListCardsFragmentInteractionListener mListener;
    private CardsAdapter adapter;

    private FragmentIdentityListener mId;

    public ListCardsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<LocalCreditCard> cards = Common.getRealm().where(LocalCreditCard.class).findAll();
        adapter = new CardsAdapter(cards, getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_cards, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardList.setLayoutManager(new LinearLayoutManager(getActivity()));
        cardList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mId.fragmentIdentity(0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListCardsFragmentInteractionListener) {
            mListener = (OnListCardsFragmentInteractionListener) context;
            mId = (FragmentIdentityListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mId = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListCardsFragmentInteractionListener {
    }
}
