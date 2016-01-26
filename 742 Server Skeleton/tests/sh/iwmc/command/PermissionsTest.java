package sh.iwmc.command;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by Brent on 01/26/2016.
 */
public class PermissionsTest {

    private Permissions permissions;

    @Test
    public void testRegularCaseForPermissions() throws Exception {

        permissions = new Permissions(Arrays.asList(
                "sh.iwmc.administration.ban",
                "sh.iwmc.tools.*",
                "sh.iwmc.combat.instantkill",
                "org.bukkit.ban"
        ));

        assertTrue(permissions.hasPermission("sh.iwmc.administration.ban"));
        assertTrue(permissions.hasPermission("sh.iwmc.tools.editor"));
        assertFalse(permissions.hasPermission("bitcode.not.a.permission"));
        System.out.println(permissions);
    }

    @Test
    public void testSpecialCase() {
        permissions = new Permissions(Arrays.asList("*"));

        assertTrue(permissions.hasPermission("sh.iwmc.test"));
        assertTrue(permissions.hasPermission("test"));
    }
}