package main.logic;

import main.model.Candy;

public interface ICandyDestroyListener {
    void onCandyDestroyed(Candy candy);
    void onSecondaryCandyDestroyed(Candy candy);
}
