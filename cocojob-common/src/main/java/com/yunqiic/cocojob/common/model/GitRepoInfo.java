package com.yunqiic.cocojob.common.model;

import lombok.Data;

/**
 * The class for Git Repository info.
 *
 * @author zhangchunsheng
 * @since 2021-12-02
 */
@Data
public class GitRepoInfo {
    /**
     * Address of Git repository.
     */
    private String repo;
    /**
     * Name of the branch.
     */
    private String branch;
    /**
     * username of Git.
     */
    private String username;
    /**
     * Password of Git.
     */
    private String password;
}
