create unique index uq_t_user_subject_active_external_identity
    on t_user_subject (subject_provider, external_subject_id)
    where is_valid;

drop index idx_t_user_subject_external_subject_valid;
