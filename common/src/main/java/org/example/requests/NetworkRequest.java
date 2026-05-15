package org.example.requests;

import java.io.Serializable;
import java.util.UUID;

public interface NetworkRequest extends Serializable {
    UUID getId();
}
