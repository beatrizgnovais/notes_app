ALTER TABLE notes
    ALTER COLUMN last_update TYPE TIMESTAMP WITH TIME ZONE
    USING to_timestamp(last_update);
