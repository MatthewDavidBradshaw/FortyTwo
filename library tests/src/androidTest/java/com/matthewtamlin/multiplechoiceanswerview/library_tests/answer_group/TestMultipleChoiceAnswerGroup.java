package com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.matthewtamlin.android_testing_tools.library.EspressoHelper;
import com.matthewtamlin.multiple_choice_answer_view.library.answer_group.AnswerGroup.Listener;
import com.matthewtamlin.multiple_choice_answer_view.library.answer_group.MultipleChoiceAnswerGroup;
import com.matthewtamlin.multiple_choice_answer_view.library.answer_view.AnswerView;
import com.matthewtamlin.multiple_choice_answer_view.library.answer_view.DecoratedAnswerCard;
import com.matthewtamlin.multiplechoiceanswerview.library_tests.MultipleChoiceAnswerGroupTestHarness;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group.MultipleChoiceAnswerGroupViewActions.addAnswer;
import static com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group.MultipleChoiceAnswerGroupViewActions.addAnswers;
import static com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group.MultipleChoiceAnswerGroupViewActions.allowSelectionChangesWhenMarked;
import static com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group.MultipleChoiceAnswerGroupViewActions.clearAnswers;
import static com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group.MultipleChoiceAnswerGroupViewActions.clickViewAtIndex;
import static com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group.MultipleChoiceAnswerGroupViewActions.registerListener;
import static com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group.MultipleChoiceAnswerGroupViewActions.removeAnswer;
import static com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group.MultipleChoiceAnswerGroupViewActions.setMultipleSelectionLimit;
import static com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group.MultipleChoiceAnswerGroupViewAssertions.containsNoAnswers;
import static com.matthewtamlin.multiplechoiceanswerview.library_tests.answer_group.MultipleChoiceAnswerGroupViewAssertions.containsView;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class TestMultipleChoiceAnswerGroup {
	@Rule
	public ActivityTestRule<MultipleChoiceAnswerGroupTestHarness> rule = new
			ActivityTestRule<>(MultipleChoiceAnswerGroupTestHarness.class);

	private MultipleChoiceAnswerGroup<DecoratedAnswerCard> testViewDirect;

	public ViewInteraction testViewEspresso;

	private Listener<DecoratedAnswerCard> listener1;

	private Listener<DecoratedAnswerCard> listener2;

	@Before
	public void setup() {
		testViewDirect = rule.getActivity().getTestView();
		testViewEspresso = EspressoHelper.viewToViewInteraction(testViewDirect);

		listener1 = mock(Listener.class);
		listener2 = mock(Listener.class);

		testViewEspresso.perform(registerListener(listener1));
		testViewEspresso.perform(registerListener(listener2));
		testViewEspresso.perform(registerListener(null)); // Check null safety
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAnswers_nullSupplied() {
		testViewEspresso.perform(addAnswers(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAnswers_collectionContainsNull() {
		final List<AnswerView> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(null);

		testViewEspresso.perform(addAnswers(answers));
	}

	@Test
	public void testAddAnswers_emptyCollection() {
		final List<AnswerView> answers = new ArrayList<>();

		testViewEspresso.perform(addAnswers(answers));

		testViewEspresso.check(containsNoAnswers());

		assertThat("Expected list to reflect there being no answer cards.",
				testViewDirect.getAnswers().isEmpty(), is(true));
	}

	@Test
	public void testAddAnswers_validArgument() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		testViewEspresso.perform(addAnswers(answers));

		for (final DecoratedAnswerCard card : answers) {
			testViewEspresso.check(containsView(card, true));
		}

		assertThat("Expected list to contain only the supplied answers.",
				testViewDirect.getAnswers(), is(answers));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAnswer_nullSupplied() {
		testViewEspresso.perform(addAnswer(null));
	}

	@Test
	public void testAddAnswer_validArgument() {
		final DecoratedAnswerCard view = getNewAnswerCard();

		testViewEspresso.perform(addAnswer(view));

		testViewEspresso.check(containsView(view, true));

		assertThat("Expected list to contain only only one item.",
				testViewDirect.getAnswers().size(), is(1));
		assertThat("Expected list to contain only the supplied answer.",
				testViewDirect.getAnswers().get(0), is(view));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveAnswer_nullSupplied() {
		testViewEspresso.perform(removeAnswer(null));
	}

	@Test
	public void testRemoveAnswer_viewNotInGroup() {
		final AnswerView view = getNewAnswerCard();

		testViewEspresso.perform(removeAnswer(view));
	}

	@Test
	public void testRemoveAnswer_viewContainedInGroup() {
		final AnswerView view = getNewAnswerCard();

		testViewEspresso.perform(addAnswer(view));

		testViewEspresso.check(containsView(view, true));

		testViewEspresso.perform(removeAnswer(view));

		testViewEspresso.check(containsView(view, false));

		assertThat("Expected list to contain no items.", testViewDirect.getAnswers().isEmpty(),
				is(true));
	}

	@Test
	public void testClearAnswers() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		testViewEspresso.perform(addAnswers(answers));

		for (final DecoratedAnswerCard card : answers) {
			testViewEspresso.check(containsView(card, true));
		}

		testViewEspresso.perform(clearAnswers());

		for (final DecoratedAnswerCard view : answers) {
			testViewEspresso.check(containsView(view, false));
		}

		assertThat("Expected list to contain no items.", testViewDirect.getAnswers().isEmpty(),
				is(true));
	}

	@Test
	public void testClickAnswer_selectionChangesAllowedAndMarked() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		answers.get(0).setStatus(true, false, false);
		answers.get(1).setStatus(false, false, false);

		testViewEspresso.perform(addAnswers(answers));
		testViewEspresso.perform(allowSelectionChangesWhenMarked(true));
		testViewEspresso.perform(clickViewAtIndex(0));

		assertThat("Answer 0 should be selected.", answers.get(0).isSelected(), is(true));
		assertThat("Answer 1 should not be selected.", answers.get(1).isSelected(), is(false));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(0), 0);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
	}

	@Test
	public void testClickAnswer_selectionChangesAllowedAndUnmarked() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		answers.get(0).setStatus(false, false, false);
		answers.get(1).setStatus(false, false, false);

		testViewEspresso.perform(addAnswers(answers));
		testViewEspresso.perform(allowSelectionChangesWhenMarked(true));
		testViewEspresso.perform(clickViewAtIndex(0));

		assertThat("Answer 0 should be selected.", answers.get(0).isSelected(), is(true));
		assertThat("Answer 1 should not be selected.", answers.get(1).isSelected(), is(false));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(0), 0);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
	}

	@Test
	public void testClickAnswer_selectionChangesDisallowedAndMarked() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		answers.get(0).setStatus(true, false, false);
		answers.get(1).setStatus(false, false, false);

		testViewEspresso.perform(addAnswers(answers));
		testViewEspresso.perform(allowSelectionChangesWhenMarked(false));
		testViewEspresso.perform(clickViewAtIndex(0));

		assertThat("Answer 0 should not be selected.", answers.get(0).isSelected(), is(false));
		assertThat("Answer 1 should not be selected.", answers.get(1).isSelected(), is(false));

		verifySelectedCallbackInvocations(answers.get(0), 0);
		verifySelectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(0), 0);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
	}

	@Test
	public void testClickAnswer_selectionChangesDisallowedAnUnmarked() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		answers.get(0).setStatus(false, false, false);
		answers.get(1).setStatus(false, false, false);

		testViewEspresso.perform(addAnswers(answers));
		testViewEspresso.perform(allowSelectionChangesWhenMarked(false));
		testViewEspresso.perform(clickViewAtIndex(0));

		assertThat("Answer 0 should be selected.", answers.get(0).isSelected(), is(true));
		assertThat("Answer 1 should not be selected.", answers.get(1).isSelected(), is(false));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(0), 0);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
	}

	@Test
	public void testSelectAnswer_selectionCapacityReached() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		testViewEspresso.perform(addAnswers(answers));
		testViewEspresso.perform(allowSelectionChangesWhenMarked(true));
		testViewEspresso.perform(setMultipleSelectionLimit(1));

		testViewEspresso.perform(clickViewAtIndex(0));

		assertThat("Answer 0 should be selected.", answers.get(0).isSelected(), is(true));
		assertThat("Answer 1 should not be selected.", answers.get(1).isSelected(), is(false));
		assertThat("Answer 2 should not be selected.", answers.get(2).isSelected(), is(false));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 0);
		verifySelectedCallbackInvocations(answers.get(2), 0);
		verifyDeselectedCallbackInvocations(answers.get(0), 0);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(2), 0);

		testViewEspresso.perform(clickViewAtIndex(1));

		assertThat("Answer 0 should not be selected.", answers.get(0).isSelected(), is(false));
		assertThat("Answer 1 should be selected.", answers.get(1).isSelected(), is(true));
		assertThat("Answer 2 should not be selected.", answers.get(2).isSelected(), is(false));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 1);
		verifySelectedCallbackInvocations(answers.get(2), 0);
		verifyDeselectedCallbackInvocations(answers.get(0), 1);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(2), 0);

		testViewEspresso.perform(clickViewAtIndex(2));

		assertThat("Answer 0 should not be selected.", answers.get(0).isSelected(), is(false));
		assertThat("Answer 1 should not be selected.", answers.get(1).isSelected(), is(false));
		assertThat("Answer 2 should be selected.", answers.get(2).isSelected(), is(true));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 1);
		verifySelectedCallbackInvocations(answers.get(2), 1);
		verifyDeselectedCallbackInvocations(answers.get(0), 1);
		verifyDeselectedCallbackInvocations(answers.get(1), 1);
		verifyDeselectedCallbackInvocations(answers.get(2), 0);

		testViewEspresso.perform(clickViewAtIndex(0));

		assertThat("Answer 0 should be selected.", answers.get(0).isSelected(), is(true));
		assertThat("Answer 1 should not be selected.", answers.get(1).isSelected(), is(false));
		assertThat("Answer 2 should not be selected.", answers.get(2).isSelected(), is(false));

		verifySelectedCallbackInvocations(answers.get(0), 2);
		verifySelectedCallbackInvocations(answers.get(1), 1);
		verifySelectedCallbackInvocations(answers.get(2), 1);
		verifyDeselectedCallbackInvocations(answers.get(0), 1);
		verifyDeselectedCallbackInvocations(answers.get(1), 1);
		verifyDeselectedCallbackInvocations(answers.get(2), 1);
	}

	public void testSelectAnswer_selectionCapacityNotExceeded() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		testViewEspresso.perform(addAnswers(answers));
		testViewEspresso.perform(allowSelectionChangesWhenMarked(true));
		testViewEspresso.perform(setMultipleSelectionLimit(3));

		testViewEspresso.perform(clickViewAtIndex(0));

		assertThat("Answer 0 should be selected.", answers.get(0).isSelected(), is(true));
		assertThat("Answer 1 should not be selected.", answers.get(1).isSelected(), is(false));
		assertThat("Answer 2 should not be selected.", answers.get(2).isSelected(), is(false));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 0);
		verifySelectedCallbackInvocations(answers.get(2), 0);
		verifyDeselectedCallbackInvocations(answers.get(0), 0);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(2), 0);

		testViewEspresso.perform(clickViewAtIndex(1));

		assertThat("Answer 0 should be selected.", answers.get(0).isSelected(), is(true));
		assertThat("Answer 1 should be selected.", answers.get(1).isSelected(), is(true));
		assertThat("Answer 2 should not be selected.", answers.get(2).isSelected(), is(false));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 1);
		verifySelectedCallbackInvocations(answers.get(2), 0);
		verifyDeselectedCallbackInvocations(answers.get(0), 0);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(2), 0);

		testViewEspresso.perform(clickViewAtIndex(2));

		assertThat("Answer 0 should be selected.", answers.get(0).isSelected(), is(true));
		assertThat("Answer 1 should be selected.", answers.get(1).isSelected(), is(true));
		assertThat("Answer 2 should be selected.", answers.get(2).isSelected(), is(true));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 1);
		verifySelectedCallbackInvocations(answers.get(2), 1);
		verifyDeselectedCallbackInvocations(answers.get(0), 0);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(2), 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetSelectionLimit_limitIsNegative() {
		testViewEspresso.perform(setMultipleSelectionLimit(-1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetSelectionLimit_limitIsZero() {
		testViewEspresso.perform(setMultipleSelectionLimit(0));
	}

	@Test
	public void testSetSelectionLimit_limitExceedsCurrentSelectionCount() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		testViewEspresso.perform(addAnswers(answers));
		testViewEspresso.perform(allowSelectionChangesWhenMarked(true));
		testViewEspresso.perform(setMultipleSelectionLimit(3));

		testViewEspresso.perform(clickViewAtIndex(0));
		testViewEspresso.perform(clickViewAtIndex(1));
		testViewEspresso.perform(clickViewAtIndex(2));

		testViewEspresso.perform(setMultipleSelectionLimit(1));

		assertThat("Answer 0 should not be selected.", answers.get(0).isSelected(), is(false));
		assertThat("Answer 1 should not be selected.", answers.get(1).isSelected(), is(false));
		assertThat("Answer 2 should be selected.", answers.get(2).isSelected(), is(true));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 1);
		verifySelectedCallbackInvocations(answers.get(2), 1);
		verifyDeselectedCallbackInvocations(answers.get(0), 1);
		verifyDeselectedCallbackInvocations(answers.get(1), 1);
		verifyDeselectedCallbackInvocations(answers.get(2), 0);
	}

	@Test
	public void testSetSelectionLimit_limitEqualToCurrentSelectionCount() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		testViewEspresso.perform(addAnswers(answers));
		testViewEspresso.perform(allowSelectionChangesWhenMarked(true));
		testViewEspresso.perform(setMultipleSelectionLimit(3));

		testViewEspresso.perform(clickViewAtIndex(0));
		testViewEspresso.perform(clickViewAtIndex(1));
		testViewEspresso.perform(clickViewAtIndex(2));

		testViewEspresso.perform(setMultipleSelectionLimit(3));

		assertThat("Answer 0 should be selected.", answers.get(0).isSelected(), is(true));
		assertThat("Answer 1 should be selected.", answers.get(1).isSelected(), is(true));
		assertThat("Answer 2 should be selected.", answers.get(2).isSelected(), is(true));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 1);
		verifySelectedCallbackInvocations(answers.get(2), 1);
		verifyDeselectedCallbackInvocations(answers.get(0), 0);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(2), 0);
	}

	@Test
	public void testSetSelectionLimit_limitDoesNotExceedCurrentSelectionCount() {
		final List<DecoratedAnswerCard> answers = new ArrayList<>();
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());
		answers.add(getNewAnswerCard());

		testViewEspresso.perform(addAnswers(answers));
		testViewEspresso.perform(allowSelectionChangesWhenMarked(true));
		testViewEspresso.perform(setMultipleSelectionLimit(3));

		testViewEspresso.perform(clickViewAtIndex(0));
		testViewEspresso.perform(clickViewAtIndex(1));
		testViewEspresso.perform(clickViewAtIndex(2));

		testViewEspresso.perform(setMultipleSelectionLimit(4));

		assertThat("Answer 0 should be selected.", answers.get(0).isSelected(), is(true));
		assertThat("Answer 1 should be selected.", answers.get(1).isSelected(), is(true));
		assertThat("Answer 2 should be selected.", answers.get(2).isSelected(), is(true));

		verifySelectedCallbackInvocations(answers.get(0), 1);
		verifySelectedCallbackInvocations(answers.get(1), 1);
		verifySelectedCallbackInvocations(answers.get(2), 1);
		verifyDeselectedCallbackInvocations(answers.get(0), 0);
		verifyDeselectedCallbackInvocations(answers.get(1), 0);
		verifyDeselectedCallbackInvocations(answers.get(2), 0);
	}

	/**
	 * @return a new answer card which is neither selected nor marked
	 */
	private DecoratedAnswerCard getNewAnswerCard() {
		final Context context = InstrumentationRegistry.getTargetContext();
		return new DecoratedAnswerCard(context);
	}

	private void verifySelectedCallbackInvocations(final DecoratedAnswerCard selectedView,
			final int times) {
		verify(listener1, times(times)).onAnswerSelected(testViewDirect, selectedView);
		verify(listener2, times(times)).onAnswerSelected(testViewDirect, selectedView);
	}

	private void verifyDeselectedCallbackInvocations(final DecoratedAnswerCard deselectedView,
			final int times) {
		verify(listener1, times(times)).onAnswerDeselected(testViewDirect, deselectedView);
		verify(listener2, times(times)).onAnswerDeselected(testViewDirect, deselectedView);
	}
}
