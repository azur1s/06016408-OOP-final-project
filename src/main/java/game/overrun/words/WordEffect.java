package game.overrun.words;

public sealed interface WordEffect {
    record Normal() implements WordEffect {
    }

    /**
     * Hides the last N characters of the word until the user types enough correct
     * characters to reveal them.
     */
    record Hidden(int count) implements WordEffect {
    }

    /**
     * User have to type this word N times in a row to complete it.
     */
    record Repeat(int count, int typedCount) implements WordEffect {
        public Repeat(int count) {
            this(count, 0);
        }

        public Repeat incrementTypedCount() {
            return new Repeat(count, typedCount + 1);
        }

        public boolean isCompleted() {
            return typedCount >= count;
        }
    }

    record Boss(int minLength) implements WordEffect {
        public Boss(int minLength) {
            this.minLength = minLength;
        }
    }

    static WordEffect randomEffect(WordEntity wordEntity) {
        double r = Math.random();
        if (r < 0.2) {
            // hide 40% of the characters with minimum of 1
            return new Hidden(Math.max(1, (int) (wordEntity.word.length() * 0.4)));
        } else if (r < 0.4) {
            // repeat 1-2 times randomly
            int count = (int) (Math.random() * 2) + 1;
            return new Repeat(count);
        } else {
            return new Normal();
        }
    }

    static float getScoreMultipler(WordEffect effect) {
        if (effect instanceof Hidden hiddenEffect) {
            return 1f + hiddenEffect.count() * 0.25f;
        } else if (effect instanceof Repeat repeatEffect) {
            return 1f + repeatEffect.count() * 0.25f;
        } else {
            return 1f;
        }
    }

    static float getDamageMultipler(WordEffect effect) {
        if (effect instanceof Hidden hiddenEffect) {
            return 1f + hiddenEffect.count() * 0.2f;
        } else if (effect instanceof Repeat repeatEffect) {
            return 1f + repeatEffect.count() * 0.5f;
        } else {
            return 1f;
        }
    }
}
