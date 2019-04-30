package riskOfSpire;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.helpers.RelicType;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.clapper.util.classutil.*;
import riskOfSpire.patches.ForUsableRelics.UsableRelicSlot;
import riskOfSpire.relics.Abstracts.StackableRelic;
import riskOfSpire.relics.Abstracts.UsableRelic;
import riskOfSpire.relics.Common.*;
import riskOfSpire.relics.Rare.CeremonialDagger;
import riskOfSpire.relics.Uncommon.AtGMissileMk1;
import riskOfSpire.relics.Lunar.GestureOfTheDrowned;
import riskOfSpire.relics.Uncommon.Infusion;
import riskOfSpire.relics.Uncommon.MonsterTooth;
import riskOfSpire.relics.Usable.DisposableMissileLauncher;
import riskOfSpire.relics.Usable.EffigyOfGrief;
import riskOfSpire.relics.Usable.RadarScanner;
import riskOfSpire.util.IDCheckDontTouchPls;
import riskOfSpire.util.RelicFilter;
import riskOfSpire.util.TextureLoader;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class RiskOfSpire implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        RelicGetSubscriber,
        PostInitializeSubscriber,
        PostDungeonInitializeSubscriber {
    public static final Logger logger = LogManager.getLogger(RiskOfSpire.class.getName());
    private static String modID;

    public static Properties ModSettings = new Properties();
    public static final String ENABLE_PLACEHOLDER_SETTINGS = "enablePlaceholder";
    public static boolean enablePlaceholder = true;

    private static final String MODNAME = "Risk Of Spire";
    private static final String AUTHOR = "erasels / Alchyr / Kio / Amethyst / Lobbien";
    private static final String DESCRIPTION = "A mod to add the items from Risk of Rain in the context of Slay the Spire relics.";

    public static ArrayList<Color> COLORS = new ArrayList<>(Arrays.asList(Color.MAGENTA.cpy(), Color.WHITE.cpy(), Color.BLUE.cpy(), Color.CHARTREUSE.cpy(), Color.CORAL.cpy(), Color.CYAN.cpy(), Color.FIREBRICK.cpy(), Color.FOREST.cpy(), Color.GOLD.cpy(),Color.VIOLET.cpy()));

    public static final String BADGE_IMAGE = "riskOfSpireResources/images/Badge.png";
    
    public static String makeCardPath(String resourcePath) {
        return getModID() + "Resources/images/cards/" + resourcePath;
    }
    
    public static String makeRelicPath(String resourcePath) {
        return getModID() + "Resources/images/relics/" + resourcePath;
    }
    
    public static String makeRelicOutlinePath(String resourcePath) {
        return getModID() + "Resources/images/relics/outline/" + resourcePath;
    }
    
    public static String makeOrbPath(String resourcePath) {
        return getModID() + "Resources/orbs/" + resourcePath;
    }
    
    public static String makePowerPath(String resourcePath) {
        return getModID() + "Resources/images/powers/" + resourcePath;
    }
    
    public static String makeEventPath(String resourcePath) {
        return getModID() + "Resources/images/events/" + resourcePath;
    }
    
    public RiskOfSpire() {
        logger.info("Subscribe to BaseMod hooks");
        
        BaseMod.subscribe(this);
      
        setModID("riskOfSpire");
        
        logger.info("Done subscribing");
        
        logger.info("Adding mod settings");
        ModSettings.setProperty(ENABLE_PLACEHOLDER_SETTINGS, "FALSE"); // This is the default setting. It's actually set...
        try {
            SpireConfig config = new SpireConfig("riskOfSpire", "theDefaultConfig", ModSettings); // ...right here
            // the "fileName" parameter is the name of the file MTS will create where it will save our setting.
            config.load(); // Load the setting and set the boolean to equal it
            enablePlaceholder = config.getBool(ENABLE_PLACEHOLDER_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Done adding mod settings");
        
    }

    public static void setModID(String ID) { // DON'T EDIT
        Gson coolG = new Gson(); // EY DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i hate u Gdx.files
        InputStream in = RiskOfSpire.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THIS ETHER
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // OR THIS, DON'T EDIT IT
        logger.info("You are attempting to set your mod ID as: " + ID); // NO WHY
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) { // DO *NOT* CHANGE THIS ESPECIALLY, TO EDIT YOUR MOD ID, SCROLL UP JUST A LITTLE, IT'S JUST ABOVE
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION); // THIS ALSO DON'T EDIT
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) { // NO
            modID = EXCEPTION_STRINGS.DEFAULTID; // DON'T
        } else { // NO EDIT AREA
            modID = ID; // DON'T WRITE OR CHANGE THINGS HERE NOT EVEN A LITTLE
        } // NO
        logger.info("Success! ID is " + modID); // WHY WOULD U WANT IT NOT TO LOG?? DON'T EDIT THIS.
    } // NO
    
    public static String getModID() { // NO
        return modID; // DOUBLE NO
    } // NU-UH
    
    private static void pathCheck() { // ALSO NO
        Gson coolG = new Gson(); // NNOPE DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i still hate u btw Gdx.files
        InputStream in = RiskOfSpire.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THISSSSS
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // NAH, NO EDIT
        String packageName = RiskOfSpire.class.getPackage().getName(); // STILL NO EDIT ZONE
        FileHandle resourcePathExists = Gdx.files.internal(getModID() + "Resources"); // PLEASE DON'T EDIT THINGS HERE, THANKS
        if (!modID.equals(EXCEPTION_STRINGS.DEVID)) { // LEAVE THIS EDIT-LESS
            if (!packageName.equals(getModID())) { // NOT HERE ETHER
                throw new RuntimeException(EXCEPTION_STRINGS.PACKAGE_EXCEPTION + getModID()); // THIS IS A NO-NO
            } // WHY WOULD U EDIT THIS
            if (!resourcePathExists.exists()) { // DON'T CHANGE THIS
                throw new RuntimeException(EXCEPTION_STRINGS.RESOURCE_FOLDER_EXCEPTION + getModID() + "Resources"); // NOT THIS
            }// NO
        }// NO
    }// NO
    
    
    @SuppressWarnings("unused")
    public static void initialize() {
        RiskOfSpire riskOfSpire = new RiskOfSpire();
    }
    
    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");
        
        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        
        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        
        // Create the on/off button:
        ModLabeledToggleButton enableNormalsButton = new ModLabeledToggleButton("This is the text which goes next to the checkbox.",
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont, // Position (trial and error it), color, font
                enablePlaceholder, // Boolean it uses
                settingsPanel, // The mod panel in which this button will be in
                (label) -> {}, // thing??????? idk
                (button) -> { // The actual button:
            
            enablePlaceholder = button.enabled; // The boolean true/false will be whether the button is enabled or not
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("riskOfSpire", "theDefaultConfig", ModSettings);
                config.setBool(ENABLE_PLACEHOLDER_SETTINGS, enablePlaceholder);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        settingsPanel.addUIElement(enableNormalsButton); // Add the button to the settings panel. Button is a go.
        
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);


        logger.info("Done loading badge Image and mod options");
    }
    
    @Override
    public void receiveEditRelics() {
        logger.info("Adding relics");

        //All relics are Shared relics, not character specific, so I can just do this.
        try {
            autoAddRelics();
        }
        catch (URISyntaxException | IllegalAccessException | InstantiationException | NotFoundException | CannotCompileException e)
        {
            e.printStackTrace();
        }

        logger.info("Done adding relics!");
    }
    
    @Override
    public void receiveEditCards() { 
    }

    
    @Override
    public void receiveEditStrings() {
        logger.info("Beginning to edit strings for mod with ID: " + getModID());
        
        // CardStrings
        BaseMod.loadCustomStringsFile(CardStrings.class,
                getModID() + "Resources/localization/eng/Card-Strings.json");
        
        // PowerStrings
        BaseMod.loadCustomStringsFile(PowerStrings.class,
                getModID() + "Resources/localization/eng/Power-Strings.json");
        
        // RelicStrings
        BaseMod.loadCustomStringsFile(RelicStrings.class,
                getModID() + "Resources/localization/eng/Relic-Strings.json");
        
        // Event Strings
        BaseMod.loadCustomStringsFile(EventStrings.class,
                getModID() + "Resources/localization/eng/Event-Strings.json");

        // OrbStrings
        BaseMod.loadCustomStringsFile(OrbStrings.class,
                getModID() + "Resources/localization/eng/Orb-Strings.json");
        
        logger.info("Done editing strings");
    }

    @Override
    public void receiveEditKeywords() {
        /*Gson gson = new Gson();
        String json = Gdx.files.internal(getModID() + "Resources/localization/eng/Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);
        
        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }*/

        Gson gson = new Gson();
        //String keywordStrings = Gdx.files.internal(assetPath("loc/" + languageSupport() + "/" +"aspiration-KeywordStrings.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        String keywordStrings = Gdx.files.internal(getModID() + "Resources/localization/eng/Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        Type typeToken = new TypeToken<Map<String, Keyword>>() {}.getType();

        Map<String, Keyword> keywords = (Map)gson.fromJson(keywordStrings, typeToken);

        keywords.forEach((k,v)->{
            // Keyword word = (Keyword)v;
            logger.info("Adding Keyword - " + v.NAMES[0]);
            BaseMod.addKeyword((getModID().toLowerCase() + ":"), v.PROPER_NAME, v.NAMES, v.DESCRIPTION);
        });
    }

    @Override
    public void receivePostDungeonInitialize() {
        //TODO: Remove allRoR relics from the normal pools and maybe add them to the special pools here (or do that earlier, idk)
    }

    public static String assetPath(String path)
    {
        return getModID() + "Resources/" + path;
    }

    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }

    @Override
    public void receiveRelicGet(AbstractRelic rel) {
        for (AbstractRelic r : AbstractDungeon.player.relics) {
            if (r instanceof StackableRelic) {
                ((StackableRelic)r).onRelicGet(rel);
            } else if(r instanceof UsableRelic) {
                ((UsableRelic)r).onRelicGet(rel);
            }
        }
    }



    private static void autoAddRelics() throws URISyntaxException, IllegalAccessException, InstantiationException, NotFoundException, CannotCompileException
    {
        ClassFinder finder = new ClassFinder();
        URL url = RiskOfSpire.class.getProtectionDomain().getCodeSource().getLocation();
        finder.add(new File(url.toURI()));

        ClassFilter filter =
                new AndClassFilter(
                        new NotClassFilter(new InterfaceOnlyClassFilter()),
                        new NotClassFilter(new AbstractClassFilter()),
                        new ClassModifiersClassFilter(Modifier.PUBLIC),
                        new RelicFilter()
                );
        Collection<ClassInfo> foundClasses = new ArrayList<>();
        finder.findClasses(foundClasses, filter);

        for (ClassInfo classInfo : foundClasses) {
            CtClass cls = Loader.getClassPool().get(classInfo.getClassName());

            boolean isRelic = false;
            CtClass superCls = cls;
            while (superCls != null) {
                superCls = superCls.getSuperclass();
                if (superCls == null) {
                    break;
                }
                if (superCls.getName().equals(AbstractRelic.class.getName())) {
                    isRelic = true;
                    break;
                }
            }
            if (!isRelic) {
                continue;
            }

            AbstractRelic r = (AbstractRelic) Loader.getClassPool().toClass(cls).newInstance();
            logger.info("Adding " + r.tier.name().toLowerCase() + " relic: " + r.name);

            BaseMod.addRelic(r, RelicType.SHARED);
        }
    }
}
