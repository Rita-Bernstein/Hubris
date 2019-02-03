package com.evacipated.cardcrawl.mod.hubris.cards.blue;

import basemod.abstracts.CustomCard;
import basemod.helpers.ModalChoice;
import basemod.helpers.ModalChoiceBuilder;
import basemod.helpers.TooltipInfo;
import com.evacipated.cardcrawl.mod.hubris.HubrisMod;
import com.evacipated.cardcrawl.mod.hubris.characters.FakePlayer;
import com.evacipated.cardcrawl.mod.hubris.powers.SpecializedCircuitryPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpecializedCircuitry extends CustomCard implements ModalChoice.Callback
{
    public static final String ID = "hubris:SpecializedCircuitry";
    public static final String IMG = HubrisMod.BETA_POWER;
    private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
    public static final String NAME = cardStrings.NAME;
    public static final String DESCRIPTION = cardStrings.DESCRIPTION;
    public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
    public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
    private static final int COST = 2;

    private static List<AbstractOrb> allowedOrbs = new ArrayList<>();

    private ModalChoice modal;

    static
    {
        AbstractPlayer realPlayer = AbstractDungeon.player;
        AbstractDungeon.player = new FakePlayer();

        // Base game orbs
        addOrbType(new Lightning());
        addOrbType(new Frost());
        addOrbType(new Dark());

        AbstractDungeon.player = realPlayer;
    }

    public SpecializedCircuitry()
    {
        super(ID, NAME, IMG, COST, DESCRIPTION, CardType.POWER, CardColor.BLUE, CardRarity.RARE, CardTarget.SELF);

        ModalChoiceBuilder builder = new ModalChoiceBuilder()
                .setCallback(this)
                .setColor(CardColor.BLUE)
                .setType(CardType.POWER);

        for (AbstractOrb orb : allowedOrbs) {
                builder.addOption(orb.name, EXTENDED_DESCRIPTION[0] + orb.name + EXTENDED_DESCRIPTION[1], CardTarget.SELF);
        }

        modal = builder.create();
    }

    @Override
    public List<TooltipInfo> getCustomTooltips()
    {
        String description = "Choose ";
        for (int i=0; i<allowedOrbs.size()-1; ++i) {
            description += " #y" + allowedOrbs.get(i).name + ",";
        }
        description += " or #y" + allowedOrbs.get(allowedOrbs.size()-1).name + " orbs.";

        return Arrays.asList(
                new TooltipInfo("Orb Choices", description)
        );
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m)
    {
        modal.open();
    }

    @Override
    public void optionSelected(AbstractPlayer p, AbstractMonster m, int i)
    {
        AbstractDungeon.actionManager.addToBottom(new RemoveSpecificPowerAction(p, p, SpecializedCircuitryPower.POWER_ID));
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(p, p, new SpecializedCircuitryPower(p, allowedOrbs.get(i).makeCopy())));
    }

    @Override
    public void upgrade()
    {
        if (!upgraded) {
            upgradeName();
            isInnate = true;
            rawDescription = UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    @Override
    public AbstractCard makeCopy()
    {
        return new SpecializedCircuitry();
    }

    public static void addOrbType(AbstractOrb orb)
    {
        allowedOrbs.add(orb);
    }
}
