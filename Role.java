/**
 * Holds the options for an accounts role
 * Determines the level of access and permissions granted to an account holder
 * Different roles have different actions that can be provided
 * CWE-1080: Source Code File with Excessive Number of Lines of Code
 * This file should stay below 1000 lines of code or be split into multiple files
 */
public enum Role {
    MEMBER, // Standard user with basic marketplace access
    ADMIN // Administrative user with elevated privileges
}
