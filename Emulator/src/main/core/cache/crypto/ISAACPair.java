package core.cache.crypto;

public final class ISAACPair {

    private final ISAACCipher input;

    private final ISAACCipher output;

    public ISAACPair(ISAACCipher input, ISAACCipher output) {
        this.input = input;
        this.output = output;
    }

    public ISAACCipher getInput() {
        return input;
    }

    public ISAACCipher getOutput() {
        return output;
    }

}
