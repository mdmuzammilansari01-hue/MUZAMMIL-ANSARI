package com.example.data.repository

import com.example.data.db.AppDao
import com.example.data.db.BookmarkNoteEntity
import com.example.data.db.StudentProfileEntity
import com.example.data.db.TestAttemptEntity
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Question
import com.example.data.model.StudyNote
import com.example.data.model.TestPaper
import com.example.data.model.VideoLecture
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JacRepository(private val appDao: AppDao) {

    val allAttempts: Flow<List<TestAttemptEntity>> = appDao.getAllTestAttempts()
    val allBookmarks: Flow<List<BookmarkNoteEntity>> = appDao.getAllBookmarks()
    val studentProfile: Flow<StudentProfileEntity?> = appDao.getStudentProfile()

    suspend fun saveTestAttempt(attempt: TestAttemptEntity) {
        appDao.insertTestAttempt(attempt)
    }

    suspend fun toggleBookmark(note: StudyNote, isBookmarkedCurrently: Boolean) {
        if (isBookmarkedCurrently) {
            appDao.deleteBookmark(note.id)
        } else {
            appDao.insertBookmark(
                BookmarkNoteEntity(
                    id = note.id,
                    title = note.title,
                    className = note.className,
                    subject = note.subject,
                    category = note.category,
                    isDownloaded = true
                )
            )
        }
    }

    suspend fun isNoteBookmarked(id: String): Boolean {
        return appDao.isBookmarked(id)
    }

    suspend fun saveProfile(name: String, email: String, className: String, rollNo: String, isAdmin: Boolean) {
        appDao.saveStudentProfile(
            StudentProfileEntity(
                id = 1,
                name = name,
                email = email,
                selectedClass = className,
                rollNumber = rollNo,
                isLoggedIn = true,
                isAdmin = isAdmin
            )
        )
    }

    fun getSampleTestPapers(className: String? = null, subject: String? = null): List<TestPaper> {
        val list = listOf(
            TestPaper(
                id = "jac_12_phy_01",
                title = "JAC Class 12 Physics Model Test 2026",
                className = "Class 12 Science",
                subject = "Physics",
                durationMinutes = 15,
                totalMarks = 20,
                questions = listOf(
                    Question(
                        id = "p1",
                        questionText = "What is the SI unit of electric dipole moment?",
                        options = listOf("Coulomb", "Coulomb-metre", "Volt/metre", "Ampere-metre"),
                        correctIndex = 1,
                        explanation = "Electric dipole moment p = q * 2a. The SI unit of charge q is Coulomb (C) and distance is metre (m). Thus, the unit is Coulomb-metre (C·m).",
                        subject = "Physics",
                        className = "Class 12 Science"
                    ),
                    Question(
                        id = "p2",
                        questionText = "The direction of induced current is determined by which rule/law?",
                        options = listOf("Fleming's Left Hand Rule", "Lenz's Law", "Ampere's Law", "Ohm's Law"),
                        correctIndex = 1,
                        explanation = "Lenz's law states that the direction of the induced electromotive force (emf) and current always opposes the change in magnetic flux producing it.",
                        subject = "Physics",
                        className = "Class 12 Science"
                    ),
                    Question(
                        id = "p3",
                        questionText = "Which phenomenon confirms the transverse nature of light waves?",
                        options = listOf("Interference", "Diffraction", "Polarization", "Refraction"),
                        correctIndex = 2,
                        explanation = "Polarization occurs only in transverse waves, proving that light waves vibrate perpendicular to their direction of propagation.",
                        subject = "Physics",
                        className = "Class 12 Science"
                    ),
                    Question(
                        id = "p4",
                        questionText = "What happens to the resistance of a semiconductor when temperature increases?",
                        options = listOf("Increases", "Decreases", "Remains constant", "First increases then decreases"),
                        correctIndex = 1,
                        explanation = "In semiconductors, as temperature rises, covalent bonds break creating more charge carriers (electrons and holes), thereby decreasing resistivity.",
                        subject = "Physics",
                        className = "Class 12 Science"
                    ),
                    Question(
                        id = "p5",
                        questionText = "The de Broglie wavelength of an electron accelerated through potential difference V is proportional to:",
                        options = listOf("V", "1/V", "1/√V", "√V"),
                        correctIndex = 2,
                        explanation = "λ = h / √(2m eV) = 12.27 / √V Å. Therefore, wavelength is inversely proportional to the square root of potential V.",
                        subject = "Physics",
                        className = "Class 12 Science"
                    )
                )
            ),
            TestPaper(
                id = "jac_12_chem_01",
                title = "JAC Class 12 Chemistry Solutions & Electrochemistry",
                className = "Class 12 Science",
                subject = "Chemistry",
                durationMinutes = 10,
                totalMarks = 16,
                questions = listOf(
                    Question(
                        id = "c1",
                        questionText = "Why does the physical state of a solution always depend on the physical state of the solvent?",
                        options = listOf(
                            "Because solute molecules decompose in solution",
                            "Because solvent forms the major bulk continuous phase/matrix surrounding solute particles",
                            "Because solute always has higher melting point",
                            "Because chemical reaction changes phase"
                        ),
                        correctIndex = 1,
                        explanation = "By definition, the solvent is the component present in the largest quantity. It acts as the continuous dissolving matrix holding the solute molecules, so the bulk physical state (solid, liquid, or gas) matches that of the solvent.",
                        subject = "Chemistry",
                        className = "Class 12 Science"
                    ),
                    Question(
                        id = "c2",
                        questionText = "Molarity of a solution changes with temperature because:",
                        options = listOf("Mass changes with temperature", "Volume changes with temperature", "Solute concentration drops", "Pressure remains fixed"),
                        correctIndex = 1,
                        explanation = "Molarity M = Moles of solute / Volume of solution in Litres. Volume expands or contracts with temperature, whereas mass (Mollarity/Molality) stays constant.",
                        subject = "Chemistry",
                        className = "Class 12 Science"
                    ),
                    Question(
                        id = "c3",
                        questionText = "Which colligative property is most suitable for determining molar mass of proteins and polymers?",
                        options = listOf("Relative lowering of vapour pressure", "Depression of freezing point", "Osmotic pressure", "Elevation of boiling point"),
                        correctIndex = 2,
                        explanation = "Osmotic pressure can be measured accurately at room temperature and gives appreciable values even for dilute solutions of high molar mass biomolecules.",
                        subject = "Chemistry",
                        className = "Class 12 Science"
                    ),
                    Question(
                        id = "c4",
                        questionText = "In a Galvanic cell, oxidation takes place at the:",
                        options = listOf("Anode", "Cathode", "Salt bridge", "Electrolyte surface"),
                        correctIndex = 0,
                        explanation = "In electrochemical cells (AN OX, RED CAT), Oxidation always occurs at the Anode (loss of electrons).",
                        subject = "Chemistry",
                        className = "Class 12 Science"
                    )
                )
            ),
            TestPaper(
                id = "jac_10_math_01",
                title = "JAC Class 10 Board Algebra & Trigonometry PYQ",
                className = "Class 10",
                subject = "Mathematics",
                durationMinutes = 12,
                totalMarks = 16,
                isPYQ = true,
                year = "2025 Board Exam",
                questions = listOf(
                    Question(
                        id = "m1",
                        questionText = "If sin θ + cos θ = √2 cos θ, then the value of cos θ - sin θ is:",
                        options = listOf("√2 sin θ", "√2 cos θ", "1/√2", "0"),
                        correctIndex = 0,
                        explanation = "Squaring both sides and simplifying yields cos θ - sin θ = √2 sin θ.",
                        subject = "Mathematics",
                        className = "Class 10"
                    ),
                    Question(
                        id = "m2",
                        questionText = "The 10th term of the A.P.: 2, 7, 12... is:",
                        options = listOf("45", "47", "50", "52"),
                        correctIndex = 1,
                        explanation = "a = 2, d = 5. T10 = a + 9d = 2 + 9(5) = 47.",
                        subject = "Mathematics",
                        className = "Class 10"
                    ),
                    Question(
                        id = "m3",
                        questionText = "Discriminant of quadratic equation 2x² - 4x + 3 = 0 is:",
                        options = listOf("8", "-8", "16", "-16"),
                        correctIndex = 1,
                        explanation = "D = b² - 4ac = (-4)² - 4(2)(3) = 16 - 24 = -8 (roots are non-real).",
                        subject = "Mathematics",
                        className = "Class 10"
                    ),
                    Question(
                        id = "m4",
                        questionText = "Distance between points P(2,3) and Q(4,1) is:",
                        options = listOf("2", "2√2", "4", "3√2"),
                        correctIndex = 1,
                        explanation = "d = √((4-2)² + (1-3)²) = √(4 + 4) = √8 = 2√2 units.",
                        subject = "Mathematics",
                        className = "Class 10"
                    )
                )
            ),
            TestPaper(
                id = "jac_daily_01",
                title = "JAC Board Daily Quiz - Science & Math",
                className = "Class 10",
                subject = "Biology",
                durationMinutes = 5,
                totalMarks = 8,
                isDailyQuiz = true,
                questions = listOf(
                    Question(
                        id = "dq1",
                        questionText = "Which organelle is known as the powerhouse of the cell?",
                        options = listOf("Ribosome", "Mitochondria", "Golgi apparatus", "Lysosome"),
                        correctIndex = 1,
                        explanation = "Mitochondria produce ATP through cellular respiration, earning them the nickname powerhouse of the cell.",
                        subject = "Biology",
                        className = "Class 10"
                    ),
                    Question(
                        id = "dq2",
                        questionText = "What is the pH value of human blood?",
                        options = listOf("6.0", "7.4", "8.5", "5.2"),
                        correctIndex = 1,
                        explanation = "Human blood pH is slightly alkaline, strictly maintained between 7.35 and 7.45.",
                        subject = "Biology",
                        className = "Class 10"
                    )
                )
            )
        )

        return emptyList()
    }

    fun getNotesList(): List<StudyNote> {
        return emptyList()
    }

    fun getVideoLectures(): List<VideoLecture> {
        return emptyList()
    }

    fun getLeaderboard(): List<LeaderboardEntry> {
        return emptyList()
    }
}
