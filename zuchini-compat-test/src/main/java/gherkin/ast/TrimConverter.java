package gherkin.ast;

import com.fasterxml.jackson.databind.util.StdConverter;

public class TrimConverter extends StdConverter<String, String> {
    @Override
    public String convert(String s) {
        return s == null ? null : s.trim();
    }

}
