package ch.h2m.home.automation.entity;

import java.time.Instant;

public class HueLightState {

    private final boolean state;
    private final Instant updated;

    public HueLightState(boolean state) {
        this.state = state;
        this.updated = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HueLightState that = (HueLightState) o;

        if (state != that.state) return false;
        return updated != null ? updated.equals(that.updated) : that.updated == null;
    }

    public Instant getUpdated() {
        return updated;
    }

    @Override
    public int hashCode() {
        int result = (state ? 1 : 0);
        result = 31 * result + (updated != null ? updated.hashCode() : 0);
        return result;
    }

    public boolean isState() {
        return state;
    }
}
